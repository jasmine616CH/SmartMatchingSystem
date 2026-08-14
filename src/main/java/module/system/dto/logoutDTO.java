package module.system.dto;

import lombok.Data;

/**
 * 用户登出dto
 */
@Data
public class logoutDTO {


    /**
     * 主键ID
     */
    private String userID;

    /**
     *成功的token
     */
    private String accessToken;

    /**
     * 刷新token
     */
    private String refreshToken;

}
