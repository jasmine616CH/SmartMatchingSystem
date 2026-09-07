package module.category.service;

import java.util.List;

import module.category.dto.PartCategorySaveDTO;
import module.category.vo.PartCategoryDetailVO;
import module.category.vo.PartCategoryTreeVO;

public interface PartCategoryService {

    List<PartCategoryTreeVO> queryCategoryTree();
    
    PartCategoryDetailVO queryCategoryDetail(Long catId);

    void addCategory(PartCategorySaveDTO partCategorySaveDTO);

    void updateCategory(PartCategorySaveDTO partCategorySaveDTO);

    void deleteCategory(Long catId);
}
