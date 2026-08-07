package common.result;

import common.exception.BusinessException;
import lombok.Data;

@Data
public class Result {

    private Integer code;

    private String massage;

    private Object data;

    public static Result success(Object data){
        Result result= new Result();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setData(data);
        result.setMassage("success");
        return result;
    }

    public static Result success(){
        return success(null);
    }

    public static Result error(Integer code , String massage){
        Result result = new Result();
        result.setCode(code);
        result.setData(null);
        result.setMassage(massage);
        return result;
    }

    public static Result error(BusinessException b){
        Result result = new Result();
        result.setData(null);
        result.setMassage(b.getResultCode().getMassage());
        result.setCode(b.getResultCode().getCode());
        return result;
    }

    public static Result result(Integer code , String massage){
        Result result = new Result();
        result.setCode(code);
        result.setMassage(massage);
        result.setData(null);
        return result;
    }

}
