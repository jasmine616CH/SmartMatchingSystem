package module.part.service;

import module.part.dto.PartParamValueSaveDTO;
import module.part.dto.PartParamValueUpdateDTO;

public interface PartParamValueService {
    
    void addPartParamValue(PartParamValueSaveDTO partParamValueSaveDTO);

    void updatePartParamValue(PartParamValueUpdateDTO partParamValueUpdateDTO);

    void deletePartParamValue(Long paramValId);
}
