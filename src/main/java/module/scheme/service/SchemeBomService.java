package module.scheme.service;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import module.scheme.vo.BomImportResultVO;

/**
 * 方案 BOM 导入导出业务接口
 */
public interface SchemeBomService {

    /**
     * 导出方案 BOM 为 Excel
     * <p>
     * 只导纳入正式 BOM 的明细；未认证供应商的报价不出现在导出结果里。
     *
     * @param schemeId 方案主键ID
     * @param response HTTP 响应，直接写入二进制流
     */
    void exportBom(Long schemeId, HttpServletResponse response);

    /**
     * 从 Excel 导入 BOM，总是新建一个方案
     * <p>
     * 失败行不落库，成功行在一个事务内写入；全部行失败时不创建空方案。
     *
     * @param file       Excel 文件
     * @param schemeName 方案名称，为空时用文件名
     * @return 导入结果
     */
    BomImportResultVO importBom(MultipartFile file, String schemeName);
}
