package module.scheme.vo;

import lombok.Data;

/**
 * BOM 导入失败行
 */
@Data
public class BomImportFailVO {

    /** Excel 行号（含表头，从 1 开始） */
    private Integer rowNum;

    /** 该行填写的物料编码 */
    private String partCode;

    /** 失败原因 */
    private String reason;
}
