package common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    /**
     * 状态码
     */
    SUCCESS(1 , "success"),
    ERROR(0 , "error"),

    ;

    private Integer code;
    private String message;

    private ResultCode(Integer code , String message){
        this.code=code;
        this.message=message;
    }

}
