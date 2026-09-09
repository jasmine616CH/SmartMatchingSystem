package module.part.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.storage.service.entity.PartAttachment;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartAttachmentMapper extends BaseMapper<PartAttachment> {
}
