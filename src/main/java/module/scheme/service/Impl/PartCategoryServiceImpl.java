package module.scheme.service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import module.scheme.entity.ParamTemplateField;
import module.scheme.entity.PartCategory;
import module.scheme.entity.PartInfo;
import module.scheme.entity.PartParamValue;
import module.scheme.mapper.*;
import module.scheme.service.PartCategoryService;
import module.scheme.vo.ParamVo;
import module.scheme.vo.PartCategoryTreeVO;
import module.scheme.vo.PartParameterValueVO;
import module.scheme.vo.PriceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PartCategoryServiceImpl implements PartCategoryService {

    @Autowired
    private PartCategoryMapper partCategoryMapper;

    @Autowired
    private PartInfoMapper partInfoMapper;

    @Autowired
    private ParamTemplateFieldMapper paramTemplateFieldMapper;

    @Autowired
    private PartParamValueMapper partParamValueMapper;

    @Autowired
    private PartSupplierPriceMapper partSupplierPriceMapper;

    /**
     * 返回配件三级分类表
     *
     * @return 返回三级分类表列表
     */
    @Override
    public List<PartCategoryTreeVO> getPartCategoryTree() {

        //1.动态查询
        QueryWrapper<PartCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        List<PartCategory> partCategoryList = partCategoryMapper.selectList(queryWrapper);

        //2.创建返回类型和子节点
        List<PartCategoryTreeVO> voList = BeanUtil.copyToList(partCategoryList, PartCategoryTreeVO.class);
        Map<Long, PartCategoryTreeVO> rootMap = voList.stream()
                .collect(Collectors.toMap(PartCategoryTreeVO::getCatId, Function.identity()));
        List<PartCategoryTreeVO> treeList = new ArrayList<>();

        //3.封装返回数据
        for (PartCategoryTreeVO dto :voList){
            Long parentCatId = dto.getParentCatId();
            if (parentCatId == 0L){
                treeList.add(dto);
            }
            else {
                PartCategoryTreeVO parentRoot = rootMap.get(parentCatId);
                if (parentRoot == null) {
                    if (parentRoot.getChildren() == null) {
                        parentRoot.setChildren(new ArrayList<>());
                    }
                    parentRoot.getChildren().add(dto);
                }
            }
        }

        return treeList;
    }

    /**
     * 查看配件简略信息
     * @param catId 配件id
     * @return 返回配件简略信息
     */
    @Override
    public List<List<PartParameterValueVO>> getPartParameterValue(Long catId) {

        //1.查询配件id
        List<Long> partIds = partInfoMapper.selectPartIdsByCatId(catId);
        List<PartInfo> partInfos = partInfoMapper.selectPartInfoByIds(partIds);

        //2.提取所有不重复的template_id
        List<Long> templateIds = partInfos.stream()
                .map(PartInfo::getTemplateId)
                .distinct()
                .toList();

        //3.批量一次性查询所需的字段
        List<ParamTemplateField> fieldList = paramTemplateFieldMapper.selectByTemplateIds(templateIds);
        Map<Long, List<ParamTemplateField>> fieldMap = fieldList.stream()
                .collect(Collectors.groupingBy(ParamTemplateField::getTemplateId));

        //4.批量一次性查询所有参数
        List<PartParamValue> valueList = partParamValueMapper.selectByPartIds(partIds);
        Map<Long, List<PartParamValue>> allValueMap = valueList.stream()
                .collect(Collectors.groupingBy(PartParamValue::getPartId));

        //5.批量一次性查询所有价格
        Map<Long, PriceVO> priceMap = partSupplierPriceMapper.selectPriceByPartIds(partIds);

        //6.先将配件按template_id分组
        Map<Long, List<PartInfo>> partInfoByTemplate = partInfos.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(PartInfo::getTemplateId));

        //7.构建外层List<List<PartParameterValueVO>>
        List<List<PartParameterValueVO>> voList = new ArrayList<>();

        for (Map.Entry<Long, List<PartInfo>> entry : partInfoByTemplate.entrySet()) {

            Long templateId = entry.getKey();
            List<PartInfo> templatePartInfos = entry.getValue();

            List<ParamTemplateField> fields = fieldMap.getOrDefault(templateId, new ArrayList<>());

            //构建内层List<PartParameterValueVO>
            List<PartParameterValueVO> innerList = new ArrayList<>();

            for (PartInfo partInfo : templatePartInfos) {
                if (partInfo == null) continue;

                PartParameterValueVO vo = new PartParameterValueVO();
                vo.setBrand(partInfo.getBrand());
                vo.setPartCode(partInfo.getPartCode());
                vo.setModel(partInfo.getModel());
                vo.setPriceVo(priceMap.getOrDefault(partInfo.getPartId(), new PriceVO()));

                //获取配件对应字段表后转换类型
                Map<Long, PartParamValue> valueMap = allValueMap.getOrDefault(partInfo.getPartId(), new ArrayList<>())
                        .stream()
                        .collect(Collectors.toMap(PartParamValue::getFieldId, v -> v, (v1, v2) -> v2));

                //组装动态参数
                List<ParamVo> paramVOs = new ArrayList<>();
                for (ParamTemplateField field : fields) {
                    ParamVo paramVO = new ParamVo();
                    paramVO.setParamCn(field.getParamCn());
                    paramVO.setUnit(field.getUnit());

                    PartParamValue value = valueMap.get(field.getFieldId());
                    if (value != null) {
                        //根据类型处理值
                        if ("number".equals(field.getDataType())) {
                            String numMinStr = value.getNumMin() != null ? value.getNumMin().toString() : "";
                            String numMaxStr = value.getNumMax() != null ? value.getNumMax().toString() : "";
                            paramVO.setDisplayValue(numMinStr + "-" + numMaxStr);
                        } else {
                            paramVO.setDisplayValue(value.getTextValue()!= null ? value.getTextValue() : "");
                        }
                    } else {
                        paramVO.setDisplayValue("");
                    }
                    paramVOs.add(paramVO);
                }
                vo.setCoreParam(paramVOs);
                innerList.add(vo);
            }
            voList.add(innerList);
        }

        return voList;
    }

    /**
     *
     */
    @Override
    public void storagePartBOM() {

    }
}
