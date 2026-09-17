package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * BOM 导入结果
 */
@Data
public class BomImportResultVO {

    /** 是否导入成功（至少一行成功即为 true） */
    private Boolean success;

    /** 新建的方案ID；全部行失败时为 null，不建空方案 */
    private Long schemeId;

    /** 总行数 */
    private Integer totalCount;

    /** 成功行数 */
    private Integer successCount;

    /** 失败行数 */
    private Integer failCount;

    /** 失败明细 */
    private List<BomImportFailVO> failList;
}
