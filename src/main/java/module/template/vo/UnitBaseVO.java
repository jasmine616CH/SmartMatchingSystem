package module.template.vo;

import lombok.Data;

@Data
public class UnitBaseVO {

    private String unitCode; // 存储编码（WATT、KILO_WATT...）

    private String symbol; // 显示符号（W、kW...）
    
    private String unitName; // 中文名称（瓦特、千瓦...）
}
