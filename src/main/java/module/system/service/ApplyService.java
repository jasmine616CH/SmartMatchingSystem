package module.system.service;

import module.system.dto.signInDTO;

/**
 * 系统申请账号业务接口
 * 实现系统的申请账号
 */
public interface ApplyService {

    /**
     * 用户注册
     *
     * @param request 学生教职工注册参数
     */
    void register(signInDTO request);

}
