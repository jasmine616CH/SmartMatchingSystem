package module.part.service;

import java.util.List;

import module.part.dto.PartParamValueSaveDTO;
import module.part.dto.PartParamValueUpdateDTO;
import module.part.dto.SaveParamDTO;
import module.part.vo.PartParamFieldVO;
import module.part.vo.PartParamValueVO;
import module.part.vo.SaveParamResultVO;

public interface PartParamValueService {

    PartParamValueVO queryPartParamValueDetail(Long paramValId);

    /**
     * 查询配件模板参数列表（动态：按条件表达式决定显隐）
     */
    List<PartParamFieldVO> listFieldVO(Long partId);

    /**
     * 单条参数保存（方案A核心）：新增/修改/清空三合一，返回最新列表 + 被清除的脏数据
     */
    SaveParamResultVO saveSingleParam(Long partId, SaveParamDTO dto);

    /**
     * 整套提交校验：全局必填 + 条件必填双向校验，不通过则抛异常
     */
    void submitAll(Long partId);

    @Deprecated
    void addPartParamValue(PartParamValueSaveDTO partParamValueSaveDTO);

    @Deprecated
    void updatePartParamValue(PartParamValueUpdateDTO partParamValueUpdateDTO);

    @Deprecated
    void deletePartParamValue(Long paramValId);
}
