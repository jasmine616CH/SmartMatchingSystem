package module.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.system.dto.DictTypeQueryDTO;
import module.system.dto.DictTypeSaveDTO;
import module.system.dto.DictTypeUpdateDTO;
import module.system.vo.DictDetailVO;
import module.system.vo.DictOptionVO;
import module.system.vo.DictTypeVO;

public interface SysDictTypeService {

    /**
     * 按字典编码取启用状态的字典项（供参数模板等业务取枚举选项）
     *
     * @param dictCode 字典唯一编码
     * @return 下拉选项列表
     */
    List<DictOptionVO> getEnableDictItemByDictCode(String dictCode);

    /**
     * 按字典编码取启用的字典内容（含字典名称与排序后的选项）
     * <p>对外接口用：前端只有 dictCode，需要一个通用下拉数据源。
     *
     * @param dictCode 字典唯一编码
     * @return 字典内容
     */
    DictDetailVO getEnabledDictDetail(String dictCode);

    // ==================== 字典管理页面 ====================

    /**
     * 分页查询字典类型
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    Page<DictTypeVO> queryDictTypePage(DictTypeQueryDTO dto);

    /**
     * 查询字典类型详情
     *
     * @param dictTypeId 字典类型主键ID
     * @return 字典类型
     */
    DictTypeVO queryDictTypeDetail(Long dictTypeId);

    /**
     * 新增字典类型
     *
     * @param dto 字典类型信息
     */
    void addDictType(DictTypeSaveDTO dto);

    /**
     * 修改字典类型
     *
     * @param dto 字典类型信息
     */
    void updateDictType(DictTypeUpdateDTO dto);

    /**
     * 删除字典类型
     * <p>存在字典项时不允许删除。
     *
     * @param dictTypeId 字典类型主键ID
     */
    void deleteDictType(Long dictTypeId);
}
