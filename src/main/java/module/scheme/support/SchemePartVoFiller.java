package module.scheme.support;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import module.scheme.vo.SchemePartVO;

/**
 * 方案配件明细的派生字段填充
 * <p>
 * 明细分页与 BOM 导出走的是两条不同的查询，但派生规则必须一致，
 * 故收在这里供两处共用，避免导出与页面显示对不上。
 */
public final class SchemePartVoFiller {

    /**
     * 金额固定按人民币计价，不随供应商报价的 currency 走。
     * <p>业务上本平台只做人民币采购，多币种是这张表预留的扩展位。
     */
    public static final String CURRENCY_CNY = "CNY";

    private SchemePartVoFiller() {
    }

    /**
     * 填充单个明细的派生字段
     *
     * @param vo 明细（单价与数量来自查询）
     */
    public static void fill(SchemePartVO vo) {
        vo.setPartName(buildPartName(vo.getCatName(), vo.getModel()));
        vo.setCurrency(CURRENCY_CNY);
        if (vo.getUnitPrice() != null && vo.getQuantity() != null) {
            vo.setSubtotal(vo.getUnitPrice().multiply(BigDecimal.valueOf(vo.getQuantity())));
            vo.setHasPrice(true);
        } else {
            vo.setSubtotal(null);
            vo.setHasPrice(false);
        }
    }

    /**
     * 配件展示名：分类名 + 型号
     * <p>part_info 没有展示名列，只能由分类名与型号派生。
     */
    public static String buildPartName(String catName, String model) {
        if (StringUtils.hasText(catName) && StringUtils.hasText(model)) {
            return catName + " " + model;
        }
        return StringUtils.hasText(model) ? model : catName;
    }
}
