package common.aviator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import com.googlecode.aviator.exception.ExpressionSyntaxErrorException;

import cn.hutool.core.collection.CollectionUtil;
import common.exception.BusinessException;
import common.result.ResultCode;
import module.template.entity.ParamFieldCheckRule;

public class AviatorRuleUtil {

    public static Optional<String> executeFirstErrorMsg(List<ParamFieldCheckRule> ruleList, Map<String, Object> data) {
        if (CollectionUtil.isEmpty(ruleList) || data == null) {
            return Optional.empty();
        }
        return ruleList.stream()
                .filter(rule -> 1 == rule.getStatus())
                .filter(rule -> {
                    Object executeResult = AviatorEvaluator.execute(rule.getCheckExpr(), data);
                    return !Boolean.TRUE.equals(executeResult);
                })
                .map(ParamFieldCheckRule::getErrorMsg)
                .findFirst();
    }

    /**
     * 编译并执行aviator表达式
     * 
     * @param expr aviator表达式
     * @param env  变量上下文（key:paramCode / param，value:参数值）
     * @return true=校验通过
     */
    public static boolean execute(String expr, Map<String, Object> env) {
        if (!StringUtils.hasText(expr)) {
            return true;
        }
        // 预编译
        Expression compile = AviatorEvaluator.compile(expr, true);
        Object result = compile.execute(env);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        // 非布尔结果直接判定不通过
        return false;
    }

    /**
     * 将param_field_check_rule列表拼接成完整Aviator表达式（所有规则默认并且&&）
     * 
     * @param ruleList 排序后的启用校验规则
     * @return 拼接好的完整aviator表达式
     */
    public static String buildCheckExpr(List<ParamFieldCheckRule> ruleList) {
        if (ruleList == null || ruleList.isEmpty()) {
            return null;
        }
        List<String> unitList = ruleList.stream()
                .filter(rule -> 1 == rule.getStatus())
                .map(rule -> "(" + rule.getCheckExpr() + ")")
                .collect(Collectors.toList());

        if (unitList.isEmpty()) {
            return null;
        }

        return String.join(" && ", unitList);
    }

    /**
     * 校验aviator表达式语法
     * 
     * @param expr aviator表达式
     */
    public static void validateExprSyntax(String expr) {
        if (!StringUtils.hasText(expr)) {
            return;
        }
        try {
            AviatorEvaluator.compile(expr);
        } catch (ExpressionSyntaxErrorException e) {
            throw new BusinessException(ResultCode.AVIATOR_EXPR_SYNTAX_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.AVIATOR_EXPR_COMPILE_ERROR, e.getMessage());
        }
    }
}
