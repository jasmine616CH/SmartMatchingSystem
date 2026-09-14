package module.scheme.excel;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;

import lombok.Getter;

/**
 * BOM 导入监听器
 * <p>
 * 只做「读进来并记住行号」，不做任何校验——校验与落库放在 service，
 * 便于统一控制事务边界（成功行一个事务写入，失败行不落库）。
 */
public class SchemeBomImportListener extends AnalysisEventListener<SchemeBomImportRow> {

    /** 读到的行，按文件顺序 */
    @Getter
    private final List<Row> rows = new ArrayList<>();

    @Override
    public void invoke(SchemeBomImportRow data, AnalysisContext context) {
        // 表头占第 1 行，数据从第 2 行开始
        int rowNum = context.readRowHolder().getRowIndex() + 1;
        rows.add(new Row(rowNum, data));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 无需额外处理，数据已在 rows 中
    }

    /**
     * 一行数据及其在 Excel 中的行号
     */
    public record Row(int rowNum, SchemeBomImportRow data) {
    }
}
