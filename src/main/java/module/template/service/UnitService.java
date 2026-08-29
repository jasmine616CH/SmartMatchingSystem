package module.template.service;

import java.util.List;

import module.template.vo.UnitBaseVO;

public interface UnitService {
 
    List<UnitBaseVO> getBaseUnitList();

    List<UnitBaseVO> getUnitListByBase(String unitCode);
    
}
