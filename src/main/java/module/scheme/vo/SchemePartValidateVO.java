package module.scheme.vo;

import java.util.List;

import lombok.Data;

/**
 * 单个配件的参数校验结果
 */
@Data
public class SchemePartValidateVO {

    /** 明细主键ID */
    private Long schemePartId;

    /** 配件主键ID */
    private Long partId;

    /** 配件展示名称 */
    private String partName;

    /** 该配件未通过的校验项 */
    private List<ValidateFailItemVO> failItems;
}
