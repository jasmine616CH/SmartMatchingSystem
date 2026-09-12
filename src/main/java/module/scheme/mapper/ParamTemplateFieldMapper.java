package module.scheme.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.scheme.entity.ParamTemplateField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ParamTemplateFieldMapper extends BaseMapper<ParamTemplateField> {

    /**
     * 根据模板id查询模板字段
     * @param templateIds
     * @return 返回字段信息
     */
    List<ParamTemplateField> selectByTemplateIds(List<Long> templateIds);

}

