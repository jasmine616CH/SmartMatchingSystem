package module.part.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import module.part.dto.PartParamValueQueryDTO;
import module.part.entity.PartParamValue;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PartParamValueMapper extends BaseMapper<PartParamValue> {

    /**
     * 查询某个配件所有已录入的参数值（带 param_code 和 data_type）
     */
    List<PartParamValueQueryDTO> selectValueListByPartId(@Param("partId") Long partId);

    /**
     * 按 配件 + 字段 删除参数值（单条保存"清空"与脏数据清理用）
     *
     * @return 受影响行数
     */
    int deleteByPartIdAndFieldId(
            @Param("partId") Long partId,
            @Param("fieldId") Long fieldId);
}
