package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import common.exception.BusinessException;
import common.result.ResultCode;
import module.system.dto.PasswordUpdateDTO;
import module.system.entity.SysUser;
import module.system.mapper.SysUserMapper;
import module.system.service.UpdateInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 修改信息业务接口实现类
 * 实现用户信息修改功能
 */
@Service
public class UpdateInformationServiceImpl implements UpdateInformationService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 更新用户密码
     *
     * @param dto 修改内容
     */
    @Override
    public void updateInformation(PasswordUpdateDTO dto) {

        //1.获取用户数据
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",dto.getUsername());

        //2.比对用户原密码
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(!encoder.matches(user.getPassword(), dto.getOldPassword())){
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        //3.修改用户密码
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username",dto.getUsername())
                .set("password",encoder.encode(dto.getNewPassword()));

        //4.更新数据库
        int row = sysUserMapper.update(null,updateWrapper);

    }
}
