package module.system.service;

import common.enums.UserType;
import module.system.entity.SysUser;

/**
 * 账号创建业务接口
 * <p>
 * 方案工程师自助注册 与 超管分配账号 共用这套创建逻辑，
 * 区别只在角色从哪来：前者固定为方案工程师，后者由超管指定。
 */
public interface AccountService {

    /**
     * 创建账号并绑定对应角色
     * <p>同事务完成 sys_user 与 sys_user_role 的写入，避免出现「有账号无角色」的半成品。
     *
     * @param username      登录账号
     * @param realName      真实姓名
     * @param phone         手机号
     * @param email         邮箱，可空
     * @param rawPassword   明文密码，内部做 BCrypt 加密
     * @param type          账号角色
     * @return 新建的用户
     */
    SysUser createAccount(String username, String realName, String phone,
            String email, String rawPassword, UserType type);
}
