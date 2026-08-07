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
    private String massage;

    private ResultCode(Integer code , String massage){
        this.code=code;
        this.massage= this.massage;
    }

}
