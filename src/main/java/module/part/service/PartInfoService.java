package module.part.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.part.dto.PartInfoSaveDTO;
import module.part.dto.PartInfoUpdateDTO;
import module.part.vo.PartInfoListVO;

public interface PartInfoService {
    
    Page<PartInfoListVO> queryPartInfoList(Long catId,Integer pageNum,Integer pageSize);

    void addPartInfo(PartInfoSaveDTO partInfoSaveDTO);
    
    void updatePartInfo(PartInfoUpdateDTO partInfoUpdateDTO);
}
