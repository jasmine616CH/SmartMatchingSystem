package module.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import module.template.entity.ParamTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ParamTemplateMapper extends BaseMapper<ParamTemplate> {

    /**
     * 根据模板名称统计数量，判断名称是否重复
     */
    @Select("SELECT COUNT(1) FROM param_template WHERE template_name = #{templateName}")
    long countByTemplateName(@Param("templateName") String templateName);

    // /**
    //  * 统计同名模板数量（排除自身）
    //  */
    // @Select("SELECT COUNT(1) FROM param_template WHERE template_name = #{templateName} AND template_id != #{templateId}")
    // long countTemplateNameExcludeSelf(
    //         @Param("templateName") String templateName,
    //         @Param("templateId") Long templateId);

    /**
     * 查询模板详情（关联查询审批人名称）
     */
    ParamTemplate getTemplateDetailWithAuditName(@Param("templateId") Long templateId);
}
