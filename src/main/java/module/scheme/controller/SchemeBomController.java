package module.scheme.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import common.enums.OperateModule;
import common.enums.OperateType;
import common.result.Result;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module.scheme.service.SchemeBomService;
import module.scheme.vo.BomImportResultVO;
import module.system.annotation.OperateLog;

/**
 * 方案 BOM 导入导出控制器
 */
@RequestMapping("/api/scheme")
@RequiredArgsConstructor
@RestController
@Validated
@Slf4j
public class SchemeBomController {

    private final SchemeBomService schemeBomService;

    /**
     * 导出方案 BOM 为 Excel
     * <p>
     * 返回裸二进制流而非统一响应体，前端需用 responseType:'blob' 单独处理。
     *
     * @param schemeId 方案主键ID
     * @param response HTTP 响应
     */
    @OperateLog(operateDesc = "导出方案BOM", operateType = OperateType.EXPORT, operateModule = OperateModule.SCHEME)
    @GetMapping("/{schemeId}/bom/export")
    public void exportBom(
            @NotNull(message = "schemeId不能为空") @PathVariable("schemeId") Long schemeId,
            HttpServletResponse response) {
        schemeBomService.exportBom(schemeId, response);
    }

    /**
     * 从 Excel 导入 BOM，总是新建一个方案
     *
     * @param file       Excel 文件
     * @param schemeName 方案名称，为空时用文件名
     * @return 导入结果
     */
    @OperateLog(operateDesc = "导入方案BOM", operateType = OperateType.IMPORT, operateModule = OperateModule.SCHEME)
    @PostMapping("/bom/import")
    public Result<BomImportResultVO> importBom(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "schemeName", required = false) String schemeName) {
        return Result.success(schemeBomService.importBom(file, schemeName));
    }
}
