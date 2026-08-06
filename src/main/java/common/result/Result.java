package common.result;

import lombok.Data;

@Data
public class Result {

    private Integer code;

    private String message;

    private Object data;

    public static Result success(Object data){
        Result result= new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setData(data);
        result.setMessage("success");
        return result;
    }

    public static Result success(){
        return success(null);
    }

    public static Result error(Integer code , String message){
        Result result = new Result();
        result.setCode(code);
        result.setData(null);
        result.setMessage(message);
        return result;
    }

    public static Result error()

}
