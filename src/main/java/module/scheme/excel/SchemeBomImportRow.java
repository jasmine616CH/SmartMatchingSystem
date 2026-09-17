package module.scheme.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * BOM 导入一行
 * <p>
 * 只按物料编码匹配配件，其余列仅作参考不参与落库
 * （配件名称/品牌/型号等以库中数据为准，避免导入文件与库不一致）。
 * 数量用 String 接，容忍「2」「2.0」等 Excel 里的数字格式差异。
 */
@Data
@ExcelIgnoreUnannotated
public class SchemeBomImportRow {

    /** 物料编码，匹配 part_info.part_code */
    @ExcelProperty("物料编码")
    private String partCode;

    /** 数量，留空按 1 处理 */
    @ExcelProperty("数量")
    private String quantity;
}
