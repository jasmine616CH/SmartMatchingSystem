package module.scheme.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartBomDTO {

    private String schemeName;

    private String wholeCarReq;

    private String remark;

    private BigDecimal matchScore;

    private Integer isSelectBom;

}
