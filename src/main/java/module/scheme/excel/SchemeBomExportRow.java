package module.scheme.excel;

import java.math.BigDecimal;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;

import lombok.Data;

/**
 * BOM 导出一行
 * <p>
 * 列顺序即表头顺序；未标注 @ExcelProperty 的字段不导出。
 */
@Data
@ExcelIgnoreUnannotated
public class SchemeBomExportRow {

    /** 序号 */
    @ExcelProperty("序号")
    @ColumnWidth(6)
    private Integer index;

    /** 物料编码 */
    @ExcelProperty("物料编码")
    @ColumnWidth(20)
    private String partCode;

    /** 配件名称 */
    @ExcelProperty("配件名称")
    @ColumnWidth(24)
    private String partName;

    /** 所属分类 */
    @ExcelProperty("所属分类")
    @ColumnWidth(16)
    private String catName;

    /** 品牌 */
    @ExcelProperty("品牌")
    @ColumnWidth(14)
    private String brand;

    /** 型号 */
    @ExcelProperty("型号")
    @ColumnWidth(20)
    private String model;

    /** 数量 */
    @ExcelProperty("数量")
    @ColumnWidth(8)
    private Integer quantity;

    /** 供应商 */
    @ExcelProperty("供应商")
    @ColumnWidth(20)
    private String supplierName;

    /** 币种 */
    @ExcelProperty("币种")
    @ColumnWidth(8)
    private String currency;

    /** 单价 */
    @ExcelProperty("单价")
    @ColumnWidth(12)
    private BigDecimal unitPrice;

    /** 小计 */
    @ExcelProperty("小计")
    @ColumnWidth(14)
    private BigDecimal subtotal;

    /** 最小起订量 */
    @ExcelProperty("MOQ")
    @ColumnWidth(8)
    private Integer moq;

    /** 交付周期（天） */
    @ExcelProperty("交付周期(天)")
    @ColumnWidth(12)
    private Integer leadTime;
}
