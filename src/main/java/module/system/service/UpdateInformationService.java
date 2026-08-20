package module.system.service;

import module.system.dto.PasswordUpdateDTO;

/**
 * 修改信息业务接口
 * 实现用户信息修改功能
 */
public interface UpdateInformationService {

    /**
     * 更新用户密码
     *
     * @param dto 修改内容
     */
    void updateInformation(PasswordUpdateDTO dto);

}
