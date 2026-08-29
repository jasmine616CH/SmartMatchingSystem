package module.template.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import lombok.RequiredArgsConstructor;
import module.template.entity.SysUnit;
import module.template.mapper.SysUnitMapper;
import module.template.service.UnitService;
import module.template.vo.UnitBaseVO;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final SysUnitMapper sysUnitMapper;

    @Override
    public List<UnitBaseVO> getBaseUnitList() {
        LambdaQueryWrapper<SysUnit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUnit::getUnitCode, SysUnit::getSymbol, SysUnit::getUnitName)
                .eq(SysUnit::getEnable, 1)
                .apply("base_unit_code = unit_code")
                .orderByAsc(SysUnit::getSort)
                .orderByAsc(SysUnit::getUnitCode);

        List<SysUnit> unitList = sysUnitMapper.selectList(queryWrapper);
        return unitList.stream().map(this::convertToUnitBaseVO).collect(Collectors.toList());
    }

    @Override
    public List<UnitBaseVO> getUnitListByBase(String unitCode) {
        LambdaQueryWrapper<SysUnit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(SysUnit::getUnitCode, SysUnit::getSymbol, SysUnit::getUnitName)
                .eq(SysUnit::getBaseUnitCode, unitCode)
                .orderByAsc(SysUnit::getSort)
                .orderByAsc(SysUnit::getUnitCode);
        List<SysUnit> unitList = sysUnitMapper.selectList(queryWrapper);
        return unitList.stream().map(this::convertToUnitBaseVO).collect(Collectors.toList());
    }

    private UnitBaseVO convertToUnitBaseVO(SysUnit entity) {
        UnitBaseVO vo = new UnitBaseVO();
        vo.setUnitCode(entity.getUnitCode());
        vo.setSymbol(entity.getSymbol());
        vo.setUnitName(entity.getUnitName());
        return vo;
    }
}
