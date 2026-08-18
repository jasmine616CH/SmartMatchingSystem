package module.system.service;

import module.system.dto.AccountDTO;
import module.system.dto.QueryUserInformationDTO;
import module.system.vo.QueryInformationVo;

import java.util.List;

/**
 * 管理员业务接口
 * 处理密码重置、账号冻结解冻等问题
 */
public interface AdminService {

    /**
     * 获取用户列表
     *
     * @return 返回用户信息
     */
    List<QueryInformationVo> getUserList(QueryUserInformationDTO dto);

    /**
     * 冻结账号
     *
     * @param dto 账号信息
     */
    void freezeAccount(AccountDTO dto);

    /**
     * 重置密码
     *
     * @param dto 账号信息
     */
    void resetPassword(AccountDTO dto);

}
