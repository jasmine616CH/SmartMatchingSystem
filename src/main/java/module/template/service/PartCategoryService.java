package module.template.service;

import java.util.List;

import module.template.dto.PartCategorySaveDTO;
import module.template.vo.PartCategoryDetailVO;
import module.template.vo.PartCategoryTreeVO;

public interface PartCategoryService {

    List<PartCategoryTreeVO> queryCategoryTree();
    
    PartCategoryDetailVO queryCategoryDetail(Long catId);

    void addCategory(PartCategorySaveDTO partCategorySaveDTO);

    void updateCategory(PartCategorySaveDTO partCategorySaveDTO);

    void deleteCategory(Long catId);
}
