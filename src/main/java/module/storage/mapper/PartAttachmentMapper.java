package module.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.storage.entity.PartAttachment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应part_attachment表
 */
@Mapper
public interface PartAttachmentMapper extends BaseMapper<PartAttachment> {

    @Insert("insert into ugvc_db.part_attachment (att_id, part_id, user_id, file_name, file_type, file_size, object_name) " +
            "values (#{dto.attId}, #{dto.partId}, #{dto.userId}, #{fileName}, #{fileTypr}, #{fileSize}, #{objectName})")
    void insertByFileName( PartAttachment dto);

}
