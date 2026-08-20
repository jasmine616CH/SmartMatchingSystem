package module.system.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryUserInformationDTO {

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    private String userType;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$" , message = "手机号格式不正确，请输入正确的格式")
    private String phone;

    /**
     * 邮箱
     */
    @Pattern(regexp = "^[A-Za-z0-9._-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-])*+\\.[A-Za-z0-9-]{2,6}$",
            message = "邮箱格式不正确，请输入正确格式")
    private String email;

    /**
     * 账号状态
     */
    @Pattern(regexp = "^[0-1]")
    private Integer status;


    //分页查询

    /**
     * 每页最大条数
     */
    @Min(value = 1 , message = "每页最小条数为1")
    @Max(value = 100 , message = "每页最大数为100")
    private Integer pageSize;

    /**
     * 页码数
     */
    @Min(value = 1 , message = "页码最小数为1")
    private Integer pageNum;

}
