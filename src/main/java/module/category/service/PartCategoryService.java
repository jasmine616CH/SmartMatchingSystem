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

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param catId 分类主键ID
     */
    void submitAudit(Long catId);

    /**
     * 撤回：已发布 → 草稿
     *
     * @param catId 分类主键ID
     */
    void revoke(Long catId);

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param catId 分类主键ID
     */
    void cancelSubmit(Long catId);
}
