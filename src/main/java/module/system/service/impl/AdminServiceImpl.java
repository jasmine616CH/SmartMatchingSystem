package module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import module.system.dto.AccountDTO;
import module.system.dto.AddAccountDTO;
import module.system.dto.QueryUserInformationDTO;
import module.system.entity.SysUser;
import module.system.mapper.SysUserMapper;
import module.system.service.AdminService;
import module.system.vo.QueryAccountVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;


import static com.baomidou.mybatisplus.extension.toolkit.Db.page;

/**
 * 管理员业务接口实现类
 * 处理密码重置、账号冻结解冻等问题
 */
@Service
public class AdminServiceImpl implements AdminService {

    /**
     * 定义密码池
     */
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 创建静态实例
     */
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取用户列表
     * @param dto 查找用户信息
     * @return 用户列表
     */
    @Override
    public List<QueryAccountVo> getUserList(QueryUserInformationDTO dto) {

        //1.构造条件构造器
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        //2.动态拼接条件
        wrapper.like(StringUtils.hasText(dto.getRealName()),SysUser::getRealName,dto.getRealName())
                .eq(dto.getUsername()!=null,SysUser::getUsername,dto.getUsername())
                .eq(dto.getPhone()!=null,SysUser::getPhone,dto.getPhone())
                .eq(dto.getUserType()!=null,SysUser::getUserType,dto.getUserType())
                .eq(dto.getEmail()!=null,SysUser::getEmail,dto.getEmail())
                .eq(dto.getStatus()!=null,SysUser::getStatus,dto.getStatus());

        //3.执行查询
        List<SysUser> list;
        Page<SysUser> page;
        if (dto.getPageNum()!=null&&dto.getPageSize()!=null){
            page = new Page<>(dto.getPageNum(), dto.getPageSize());
        } else {
            page = new Page<>(1, 10);
        }
        list = page(page , wrapper).getRecords();

        //4.转换为Vo并返回
        return list.stream().map(user ->{
            QueryAccountVo vo =new QueryAccountVo();
            BeanUtils.copyProperties(user , vo);
            return vo;
        }).collect(Collectors.toList());



    }

    /**
     * 冻结账户
     * @param dto 账号信息
     */
    @Override
    public void freezeAccount(AccountDTO dto) {

        //1.检查用户名是否为空
        if (dto == null || dto.getUsername() == null) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        //2.检查用户是否存在
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        //3.检查账户是否已被冻结
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被冻结，无需重复操作");
        }

        //4.冻结账户
        UpdateWrapper<SysUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("username", dto.getUsername())
                .set("status", 0);

    }

    /**
     * 冻结账户
     * @param dto 账号信息
     */
    @Override
    public void unfreezeAccount(AccountDTO dto) {

        //1.检查用户名是否为空
        if (dto == null || dto.getUsername() == null) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        //2.检查用户是否存在
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        //3.检查账户是否已被冻结
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被冻结，无需重复操作");
        }

        //4.冻结账户
        UpdateWrapper<SysUser> wrapper = new UpdateWrapper<>();
        wrapper.eq("username", dto.getUsername())
                .set("status", 1);

    }

    /**
     * 重置密码
     * @param dto 账号信息
     */
    @Override
    public String resetPassword(AccountDTO dto) {

        //1.生成8位随机密码
        StringBuilder password = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(CHARS.length());
            password.append(CHARS.charAt(index));
        }

        //2.获取用户数据
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",dto.getUsername());

        //3.修改用户密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        UpdateWrapper<SysUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username",dto.getUsername())
                .set("password",encoder.encode(password));

        //4.更新数据库
        int row = sysUserMapper.update(null,updateWrapper);

        return password.toString();
    }

    /**
     * 新增账号
     * @param dto 账号信息
     */
    @Override
    public void addNewAccount(AddAccountDTO dto) {
        sysUserMapper.addAccount(dto);
    }
}
