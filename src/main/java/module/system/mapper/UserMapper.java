package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.dto.signInDTO;
import module.system.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应系统用户表 (user)
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 注册用户
     * @param dto 方案工程师注册
     */
    @Insert("insert into ugvc_db.user(user_id, username, password, real_name, phone, email, user_type) " +
            "VALUES (#{user_id},#{username},#{password},#{real_name},#{phone},#{email},#{user_type})")
    void add(signInDTO dto);
}
