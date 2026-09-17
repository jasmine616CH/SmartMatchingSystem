package module.part.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.part.dto.PartInfoSaveDTO;
import module.part.dto.PartInfoUpdateDTO;
import module.part.vo.PartInfoDetailVO;
import module.part.vo.PartInfoListVO;

public interface PartInfoService {
    
    Page<PartInfoListVO> queryPartInfoList(Long catId,Integer pageNum,Integer pageSize);

    /**
     * 查询配件档案详情
     *
     * @param partId 配件主键ID
     * @return 配件详情
     */
    PartInfoDetailVO queryPartInfoDetail(Long partId);

    void addPartInfo(PartInfoSaveDTO partInfoSaveDTO);
    
    void updatePartInfo(PartInfoUpdateDTO partInfoUpdateDTO);

    void deletePartInfo(Long partId);

    /**
     * 提交审核：草稿 → 待审核
     *
     * @param partId 配件主键ID
     */
    void submitAudit(Long partId);

    /**
     * 撤回：已发布 → 草稿
     *
     * @param partId 配件主键ID
     */
    void revoke(Long partId);

    /**
     * 撤销申请：待审核 → 草稿
     *
     * @param partId 配件主键ID
     */
    void cancelSubmit(Long partId);
}
