package module.system.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import module.system.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    List<String> getRoleKeysByUserId(Long userId);
}