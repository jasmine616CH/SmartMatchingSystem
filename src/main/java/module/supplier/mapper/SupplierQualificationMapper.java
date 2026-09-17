package module.supplier.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import module.supplier.dto.SupplierQualificationQueryDTO;
import module.supplier.entity.SupplierQualification;
import module.supplier.vo.SupplierQualificationVO;

/**
 * 对应供应商资质档案表(supplier_qualification)
 * <p>
 * 单表增删改走 BaseMapper；列表联查（取所属供应商名称）走 XML。
 */
@Mapper
public interface SupplierQualificationMapper extends BaseMapper<SupplierQualification> {

    /**
     * 分页查询供应商资质列表（联表带出所属供应商名称）
     * <p>
     * page 必须是首个入参，分页插件据此改写 SQL 并回填 total。
     *
     * @param page 分页对象
     * @param dto  查询条件，全部可选
     * @return 分页结果
     */
    Page<SupplierQualificationVO> selectQualificationPage(Page<SupplierQualificationVO> page,
            @Param("dto") SupplierQualificationQueryDTO dto);
}
