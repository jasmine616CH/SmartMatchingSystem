package module.compat.service;

import module.compat.vo.ConflictResultVO;

/**
 * 方案跨配件兼容性冲突检测业务接口
 */
public interface ConflictCheckService {

    /**
     * 对指定方案执行跨配件兼容性检测
     * <p>
     * 检测结果会覆盖式写入 {@code scheme_conflict_log}（先清空该方案的旧日志再写），
     * 因此重复调用不会累积重复记录。
     *
     * @param schemeId 方案主键ID
     * @return 检测结果
     */
    ConflictResultVO checkScheme(Long schemeId);
}
