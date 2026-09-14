package module.supplier.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.supplier.dto.SupplierQueryDTO;
import module.supplier.entity.Supplier;
import module.supplier.vo.SupplierListVO;

/**
 * 对应供应商主体表(supplier)
 * <p>
 * 原实现用注解写了 @Insert / @Update，存在列名与参数名不匹配、硬编码库名、
 * 一个方法挂两个 @Update 等问题，已全部移除：单表增删改走 BaseMapper，
 * 列表联查（需要 sys_user 取审批人姓名）走 XML。
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {

    /**
     * 分页查询供应商列表（联表带出审批人姓名，支持按联系人姓名筛选）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total。
     *
     * @param page 分页对象
     * @param dto  查询条件，全部可选
     * @return 分页结果
     */
    Page<SupplierListVO> selectSupplierPage(Page<SupplierListVO> page,
            @Param("dto") SupplierQueryDTO dto);
}
