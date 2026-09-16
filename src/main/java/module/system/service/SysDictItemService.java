package module.system.service;

import java.util.List;

import module.system.dto.DictItemSaveDTO;
import module.system.dto.DictItemUpdateDTO;
import module.system.vo.DictItemVO;

/**
 * 字典项业务接口
 */
public interface SysDictItemService {

    /**
     * 查询某字典下的全部字典项（按 sort 升序）
     *
     * @param dictTypeId 字典类型ID
     * @return 字典项列表
     */
    List<DictItemVO> queryItemList(Long dictTypeId);

    /**
     * 新增字典项
     *
     * @param dto 字典项信息
     */
    void addItem(DictItemSaveDTO dto);

    /**
     * 修改字典项
     *
     * @param dto 字典项信息
     */
    void updateItem(DictItemUpdateDTO dto);

    /**
     * 删除字典项
     *
     * @param dictItemId 字典项主键ID
     */
    void deleteItem(Long dictItemId);
}
