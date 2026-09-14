package module.scheme.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import common.exception.BusinessException;
import common.result.ResultCode;
import common.until.SecurityUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.part.entity.PartInfo;
import module.part.mapper.PartInfoMapper;
import module.scheme.entity.SchemePart;
import module.scheme.entity.SelectionScheme;
import module.scheme.excel.SchemeBomExportRow;
import module.scheme.excel.SchemeBomImportListener;
import module.scheme.excel.SchemeBomImportRow;
import module.scheme.mapper.SchemePartMapper;
import module.scheme.mapper.SelectionSchemeMapper;
import module.scheme.service.SchemeBomService;
import module.scheme.support.SchemePartVoFiller;
import module.scheme.vo.BomImportFailVO;
import module.scheme.vo.BomImportResultVO;
import module.scheme.vo.SchemePartVO;

/**
 * 方案 BOM 导入导出业务实现类
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SchemeBomServiceImpl implements SchemeBomService {

    private final SelectionSchemeMapper selectionSchemeMapper;

    private final SchemePartMapper schemePartMapper;

    private final PartInfoMapper partInfoMapper;

    /** 导出文件名中的时间戳格式 */
    private static final DateTimeFormatter FILE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    /** 导入文件大小上限：10MB */
    private static final long MAX_IMPORT_SIZE = 10L * 1024 * 1024;

    /** 允许的导入文件后缀 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("xlsx", "xls", "csv");

    /** 正式 BOM 标记 */
    private static final int IS_SELECT_BOM = 1;

    /** 配件参与选型的状态值 */
    private static final int PUBLISHING_STATUS_PUBLISHED = 1;

    /**
     * 导出方案 BOM 为 Excel
     */
    @Override
    public void exportBom(Long schemeId, HttpServletResponse response) {
        if (schemeId == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "schemeId 不能为空");
        }
        SelectionScheme scheme = selectionSchemeMapper.selectById(schemeId);
        if (scheme == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST, "方案不存在");
        }

        // 只导纳入正式BOM的明细；无已认证报价的行 supplierName/unitPrice 为空，
        // 由查询的 LEFT JOIN 决定，属于预期行为
        List<SchemePartVO> parts = schemePartMapper.selectBomParts(schemeId);

        List<SchemeBomExportRow> rows = new ArrayList<>();
        int index = 1;
        for (SchemePartVO part : parts) {
            // 派生字段（配件名称/小计/币种）由分页查询之外的路径取不到，这里补一次，
            // 保证导出内容与页面显示一致
            SchemePartVoFiller.fill(part);

            SchemeBomExportRow row = new SchemeBomExportRow();
            row.setIndex(index++);
            row.setPartCode(part.getPartCode());
            row.setPartName(part.getPartName());
            row.setCatName(part.getCatName());
            row.setBrand(part.getBrand());
            row.setModel(part.getModel());
            row.setQuantity(part.getQuantity());
            row.setSupplierName(part.getSupplierName());
            row.setCurrency(part.getCurrency());
            row.setUnitPrice(part.getUnitPrice());
            row.setSubtotal(part.getSubtotal());
            row.setMoq(part.getMoq());
            row.setLeadTime(part.getLeadTime());
            rows.add(row);
        }

        String fileName = "BOM_" + sanitizeFileName(scheme.getSchemeName()) + "_"
                + LocalDateTime.now().format(FILE_TIME_FORMAT) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 中文文件名需要 URL 编码，否则浏览器侧会乱码
        response.setHeader("Content-Disposition",
                "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        try {
            EasyExcel.write(response.getOutputStream(), SchemeBomExportRow.class)
                    .sheet("BOM")
                    .doWrite(rows);
        } catch (IOException e) {
            log.error("导出BOM失败，schemeId={}", schemeId, e);
            throw new BusinessException(ResultCode.ERROR, "导出BOM失败");
        }
    }

    /**
     * 从 Excel 导入 BOM，总是新建一个方案
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BomImportResultVO importBom(MultipartFile file, String schemeName) {
        checkImportFile(file);

        List<SchemeBomImportListener.Row> rows = readRows(file);
        String finalSchemeName = resolveSchemeName(schemeName, file);
        // 方案名唯一，与手动保存选配结果保持一致
        LambdaQueryWrapper<SelectionScheme> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SelectionScheme::getSchemeName, finalSchemeName);
        if (selectionSchemeMapper.selectCount(nameWrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE, "方案名【" + finalSchemeName + "】已存在");
        }

        List<BomImportFailVO> failList = new ArrayList<>();
        List<Long> successPartIds = new ArrayList<>();
        List<Integer> successQuantities = new ArrayList<>();
        // 同一分类只能一款：既在文件内部去重，也在成功集合内去重
        Set<Long> usedCatIds = new HashSet<>();

        for (SchemeBomImportListener.Row row : rows) {
            SchemeBomImportRow data = row.data();
            String partCode = data == null || data.getPartCode() == null
                    ? null : data.getPartCode().trim();
            if (!StringUtils.hasText(partCode)) {
                failList.add(buildFail(row.rowNum(), partCode, "物料编码为空"));
                continue;
            }

            Integer quantity;
            try {
                quantity = parseQuantity(data.getQuantity());
            } catch (NumberFormatException e) {
                failList.add(buildFail(row.rowNum(), partCode, "数量格式不正确"));
                continue;
            }
            if (quantity == null || quantity < 1) {
                failList.add(buildFail(row.rowNum(), partCode, "数量必须大于等于1"));
                continue;
            }

            PartInfo part = loadPartByCode(partCode);
            if (part == null) {
                failList.add(buildFail(row.rowNum(), partCode, "物料编码不存在或未发布"));
                continue;
            }
            if (!usedCatIds.add(part.getCatId())) {
                failList.add(buildFail(row.rowNum(), partCode, "同一三级分类下已存在其他配件"));
                continue;
            }
            successPartIds.add(part.getPartId());
            successQuantities.add(quantity);
        }

        BomImportResultVO result = new BomImportResultVO();
        result.setTotalCount(rows.size());
        result.setSuccessCount(successPartIds.size());
        result.setFailCount(failList.size());
        result.setFailList(failList);

        // 全部行失败时不建空方案
        if (successPartIds.isEmpty()) {
            result.setSuccess(false);
            result.setSchemeId(null);
            return result;
        }

        SelectionScheme scheme = new SelectionScheme();
        scheme.setSchemeName(finalSchemeName);
        scheme.setUserId(SecurityUtils.getCurrentUserId());
        // 该列 NOT NULL，导入没有整车需求录入步骤，存空对象
        scheme.setWholeCarReq("{}");
        scheme.setRemark("由BOM导入生成");
        if (selectionSchemeMapper.insert(scheme) == 0) {
            throw new BusinessException(ResultCode.ERROR, "创建方案失败");
        }

        for (int i = 0; i < successPartIds.size(); i++) {
            SchemePart schemePart = new SchemePart();
            schemePart.setSchemeId(scheme.getSchemeId());
            schemePart.setPartId(successPartIds.get(i));
            schemePart.setQuantity(successQuantities.get(i));
            schemePart.setMatchScore(BigDecimal.ZERO);
            schemePart.setIsSelectBom(IS_SELECT_BOM);
            schemePartMapper.insert(schemePart);
        }

        result.setSuccess(true);
        result.setSchemeId(scheme.getSchemeId());
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验导入文件：非空、后缀合法、大小不超限
     */
    private void checkImportFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "导入文件不能为空");
        }
        if (file.getSize() > MAX_IMPORT_SIZE) {
            throw new BusinessException(ResultCode.PARAM_RANGE_ERROR, "导入文件不能超过10MB");
        }
        String originalName = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalName);
        if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BusinessException(ResultCode.PARAM_FORMAT_ERROR, "只支持 .xlsx / .xls / .csv 文件");
        }
    }

    /**
     * 读取 Excel 全部数据行
     */
    private List<SchemeBomImportListener.Row> readRows(MultipartFile file) {
        SchemeBomImportListener listener = new SchemeBomImportListener();
        try {
            EasyExcel.read(file.getInputStream(), SchemeBomImportRow.class, listener)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            log.error("读取导入文件失败", e);
            throw new BusinessException(ResultCode.PARAM_FORMAT_ERROR, "文件读取失败，请确认文件格式正确");
        }
        return listener.getRows();
    }

    /**
     * 方案名：优先用入参，否则用文件名去掉后缀
     */
    private String resolveSchemeName(String schemeName, MultipartFile file) {
        if (StringUtils.hasText(schemeName)) {
            return schemeName.trim();
        }
        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName)) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "方案名称不能为空");
        }
        int dot = originalName.lastIndexOf('.');
        return dot > 0 ? originalName.substring(0, dot) : originalName;
    }

    /**
     * 解析数量：留空按 1 处理
     *
     * @throws NumberFormatException 非数字
     */
    private Integer parseQuantity(String raw) {
        if (!StringUtils.hasText(raw)) {
            return 1;
        }
        String trimmed = raw.trim();
        // Excel 数字单元格常读成 "2.0" 这种形式
        if (trimmed.endsWith(".0")) {
            trimmed = trimmed.substring(0, trimmed.length() - 2);
        }
        return Integer.valueOf(trimmed);
    }

    /**
     * 按物料编码查已发布的配件
     */
    private PartInfo loadPartByCode(String partCode) {
        LambdaQueryWrapper<PartInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartInfo::getPartCode, partCode)
                .eq(PartInfo::getPublishingStatus, PUBLISHING_STATUS_PUBLISHED);
        return partInfoMapper.selectOne(wrapper);
    }

    private BomImportFailVO buildFail(Integer rowNum, String partCode, String reason) {
        BomImportFailVO fail = new BomImportFailVO();
        fail.setRowNum(rowNum);
        fail.setPartCode(partCode);
        fail.setReason(reason);
        return fail;
    }

    /**
     * 去掉文件名中的非法字符，避免 Content-Disposition 出错
     */
    private String sanitizeFileName(String name) {
        if (!StringUtils.hasText(name)) {
            return "scheme";
        }
        return name.replaceAll("[\\\\/:*?\"<>|\\s]", "_");
    }
}
