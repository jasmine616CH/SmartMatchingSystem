package module.review.spi;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import common.enums.BizType;
import common.exception.BusinessException;
import common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务审批处理器注册表
 * <p>
 * Spring 会把容器里所有 {@link BizAuditHandler} 实现注入进来，审批中心因此
 * 不需要知道任何具体业务类型的存在。
 * <p>
 * 重复注册同一 BizType 在构造阶段直接抛异常——把「静默取到错误的处理器」
 * 这类线上难查的问题提前成启动失败。
 */
@Slf4j
@Component
public class BizAuditHandlerRegistry {

    private final Map<BizType, BizAuditHandler> handlers;

    public BizAuditHandlerRegistry(List<BizAuditHandler> handlerList) {
        Map<BizType, BizAuditHandler> map = new EnumMap<>(BizType.class);
        for (BizAuditHandler handler : handlerList) {
            BizAuditHandler duplicated = map.put(handler.bizType(), handler);
            if (duplicated != null) {
                throw new IllegalStateException("重复注册的 BizType 处理器："
                        + handler.bizType() + " -> "
                        + duplicated.getClass().getName() + " / " + handler.getClass().getName());
            }
        }
        this.handlers = Collections.unmodifiableMap(map);
        log.info("通用审批中心已注册业务类型 {} 个：{}", map.size(), map.keySet());
    }

    /**
     * 严格查找：审批动作使用，未注册即失败。
     *
     * @param bizType 业务类型
     * @return 对应处理器
     */
    public BizAuditHandler get(BizType bizType) {
        BizAuditHandler handler = handlers.get(bizType);
        if (handler == null) {
            throw new BusinessException(ResultCode.PARAM_VALUE_INVALID, "不支持的业务类型：" + bizType);
        }
        return handler;
    }

    /**
     * 宽松查找：列表展示使用。
     * <p>未注册时返回 null 并降级展示，避免历史脏数据把列表接口打成 500。
     *
     * @param bizType 业务类型
     * @return 对应处理器，未注册返回 null
     */
    public BizAuditHandler find(BizType bizType) {
        return handlers.get(bizType);
    }
}
