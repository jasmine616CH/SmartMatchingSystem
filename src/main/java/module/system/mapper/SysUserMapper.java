package module.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.system.entity.SysUser;

/**
 * 对应系统用户表 (sys_user)
 * <p>
 * 原实现用 @Insert 写了 add / addAccount 两个方法，都存在硬伤：
 * 表名写成 {@code ugvc_db.user}（实际是 {@code sys_user}）、
 * {@code #{user_id}} 等下划线占位符与 DTO 的驼峰属性对不上、
 * {@code #{user_type}} / {@code #{status}} 在对应 DTO 里根本不存在。
 * 已全部移除，统一走 BaseMapper 的 insert/selectOne/updateById。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户ID查询其角色编码集合（sys_user_role JOIN sys_role）
     *
     * @param userId 用户ID
     * @return 角色编码列表，roleCode 不带 ROLE_ 前缀（LoginUser 会自行拼前缀）
     */
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);
}
