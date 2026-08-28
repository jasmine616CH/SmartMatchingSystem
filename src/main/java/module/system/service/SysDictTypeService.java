package module.system.service;

import java.util.List;

import module.system.vo.DictOptionVO;

public interface SysDictTypeService {
    List<DictOptionVO> getEnableDictItemByDictCode(String dictCode);
}
