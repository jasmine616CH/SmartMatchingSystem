package module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.system.dto.AddAccountDTO;
import module.system.dto.RegisterDTO;
import module.system.entity.SysUser;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 对应系统用户表 (user)
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 注册用户
     * @param dto 方案工程师注册
     */
    @Insert("insert into ugvc_db.user(user_id, username, password, real_name, phone, email, user_type) " +
            "VALUES (#{user_id},#{username},#{password},#{real_name},#{phone},#{email},#{user_type})")
    void add(RegisterDTO dto);


    /**
     * 根据用户ID查询角色权限
     * @param userId 用户ID
     * @return 角色权限列表
     */
    List<String> selectRoleKeysByUserId(Long userId);

    @Insert("insert into ugvc_db.user(user_id, username, password, real_name, phone, user_type, status) " +
            "VALUES (#{id},#{username},#{password},#{real_name},#{user_type},#{phone},#{status})")
    void addAccount(AddAccountDTO dto);
}
