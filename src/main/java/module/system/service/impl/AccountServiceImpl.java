package module.system.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import common.enums.UserType;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import module.system.entity.SysRole;
import module.system.entity.SysUser;
import module.system.entity.SysUserRole;
import module.system.mapper.SysRoleMapper;
import module.system.mapper.SysUserMapper;
import module.system.mapper.SysUserRoleMapper;
import module.system.service.AccountService;

/**
 * 账号创建业务实现类
 */
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {

    private final SysUserMapper sysUserMapper;

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper sysUserRoleMapper;

    private final PasswordEncoder passwordEncoder;

    /** 账号状态：1-正常 */
    private static final int STATUS_ENABLED = 1;

    /**
     * 创建账号并绑定对应角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysUser createAccount(String username, String realName, String phone,
            String email, String rawPassword, UserType type) {
        if (type == null) {
            throw new BusinessException(ResultCode.PARAM_IS_NULL, "账号角色不能为空");
        }
        checkDuplicate(username, phone);

        SysUser user = SysUser.builder()
                .username(username)
                .realName(realName)
                .phone(phone)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .userType(type.getDbValue())
                .status(STATUS_ENABLED)
                .build();
        if (sysUserMapper.insert(user) == 0) {
            throw new BusinessException(ResultCode.ERROR, "账号创建失败");
        }

        bindRole(user.getUserId(), type);
        return user;
    }

    // ==================== 私有方法 ====================

    /**
     * 登录账号与手机号都要求唯一
     */
    private void checkDuplicate(String username, String phone) {
        LambdaQueryWrapper<SysUser> nameWrapper = new LambdaQueryWrapper<>();
        nameWrapper.eq(SysUser::getUsername, username);
        if (sysUserMapper.selectCount(nameWrapper) > 0) {
            throw new BusinessException(ResultCode.DATA_DUPLICATE,
                    "登录账号【" + username + "】已存在");
        }
        if (StringUtils.hasText(phone)) {
            LambdaQueryWrapper<SysUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(SysUser::getPhone, phone);
            if (sysUserMapper.selectCount(phoneWrapper) > 0) {
                throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
            }
        }
    }

    /**
     * 按角色编码查出 role_id 并建立用户-角色关联
     * <p>
     * 权限校验读的是 sys_user_role JOIN sys_role，所以这一步不能省——
     * 只写 sys_user.user_type 的话用户登录后权限集是空的。
     */
    private void bindRole(Long userId, UserType type) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        // sys_role.role_code 不带 ROLE_ 前缀，正好等于枚举名
        wrapper.eq(SysRole::getRoleCode, type.name());
        SysRole role = sysRoleMapper.selectOne(wrapper);
        if (role == null) {
            throw new BusinessException(ResultCode.DATA_NOT_EXIST,
                    "系统未初始化该角色，请联系管理员：" + type.name());
        }

        SysUserRole userRole = new SysUserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getRoleId());
        if (sysUserRoleMapper.insert(userRole) == 0) {
            throw new BusinessException(ResultCode.ERROR, "账号角色绑定失败");
        }
    }
}
