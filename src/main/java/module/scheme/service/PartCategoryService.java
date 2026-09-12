package module.scheme.service;

import module.scheme.vo.PartCategoryTreeVO;
import module.scheme.vo.PartParameterValueVO;

import java.util.List;

public interface PartCategoryService {

    /**
     * 返回配件三级分类表
     * @return 返回三级分类表列表
     */
    List<PartCategoryTreeVO> getPartCategoryTree();

    /**
     * 查看配件简略信息
     * @param catId 配件id
     * @return 返回配件简略信息
     */
    List<List<PartParameterValueVO>> getPartParameterValue(Long catId);

    void storagePartBOM();
}
