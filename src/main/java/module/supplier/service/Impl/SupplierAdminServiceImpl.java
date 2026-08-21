package module.supplier.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import common.exception.BusinessException;
import common.result.ResultCode;
import module.supplier.dto.AdminSupplierDTO;
import module.supplier.dto.QuerySupplierDTO;
import module.supplier.dto.SupplierCodeDTO;
import module.supplier.entity.SupplierContact;
import module.supplier.entity.supplier;
import module.supplier.mapper.SupplierContactMapper;
import module.supplier.mapper.SupplierMapper;
import module.supplier.service.SupplierAdminService;
import module.supplier.vo.QuerySupplierVo;
import module.supplier.vo.SupplierDateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 供应商管理员业务接口实现类
 * 实现对供应商账号的管理
 */
@Service
public class SupplierAdminServiceImpl implements SupplierAdminService {

    @Autowired
    private SupplierMapper supplierMapper;

    @Autowired
    private SupplierContactMapper supplierContactMapper;

    /**
     * 查询供应商信息
     * @param dto 查询供应商信息
     * @return 查询结果
     */
    @Override
    public Page<QuerySupplierVo> searchSupplierList(QuerySupplierDTO dto) {

        //1.构造条件构造器
        LambdaQueryWrapper<supplier> wrapper = new LambdaQueryWrapper<>();

        //2.动态拼接条件
        wrapper.like(StringUtils.hasText(dto.getSupplierName()),supplier::getSupplierName,dto.getSupplierName())
                .eq(dto.getCreditCode()!=null,supplier::getCreditCode,dto.getCreditCode())
                .eq(dto.getStatus()!=null,supplier::getStatus,dto.getStatus());

        //3.构建分页查询
        Page<supplier> supplierPagepage = new Page<>(dto.getPageNum(),dto.getPageSize());
        Page<supplier> supplierPageResult = supplierMapper.selectPage(supplierPagepage , wrapper);

        //4.提取supplier表中的supplier_id
        Set<Long> supplierIds = supplierPageResult.getRecords().stream()
                .map(supplier::getSupplierId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        //5.批量查询联系人
        Map<Long,SupplierContact> supplierContactMap;
        if (!supplierIds.isEmpty()) {
            List<SupplierContact> contactList = supplierContactMapper.selectByIds(supplierIds);
            supplierContactMap = contactList.stream()
                    .collect(Collectors.toMap(SupplierContact::getSupplierId, Function.identity()));
        } else {
            supplierContactMap = new HashMap<>();
        }

        //6.数据封装返回vo
        List<QuerySupplierVo> voList = supplierPageResult.getRecords().stream()
                .map(supplier -> {
                    QuerySupplierVo vo = new QuerySupplierVo();
                    BeanUtils.copyProperties(supplier,vo);
                    SupplierContact contact = supplierContactMap.get(supplier.getSupplierId());
                    //空值处理
                    if (contact != null) {
                        vo.setContactName(contact.getName());
                        vo.setPhone(contact.getPhone());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        Page<QuerySupplierVo> voPage = new Page<>(supplierPageResult.getCurrent(),
                                                supplierPageResult.getSize(),
                                                supplierPageResult.getTotal());
        voPage.setRecords(voList);

        return voPage;
    }

    /**
     * 删除供应商
     *
     * @param dto 供应商id
     */
    @Override
    public void deleteSupplier(SupplierCodeDTO dto) {

        //1.判断是否为空
        LambdaQueryWrapper<supplier> wrapper =new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(dto.getCreditCode()),supplier::getSupplierId,dto.getCreditCode());
        if (!StringUtils.hasText(dto.getCreditCode())){
            throw new BusinessException(ResultCode.SUPPLIER_FAIL_FOUND);
        }

        //2.删除供应商
        supplierMapper.delete(wrapper);

    }

    /**
     * 修改供应商信息
     *
     * @param dto 供应商id
     */
    @Override
    public void updateSupplierDate(SupplierCodeDTO dto) {

        //1.查询供应商信息
        QuerySupplierDTO querySupplierDTO = new QuerySupplierDTO();


    }

    @Override
    public SupplierDateVo viewSupplierDate(SupplierCodeDTO dto) {
        return null;
    }

    /**
     * 添加供应商
     * @param dto 供应商信息
     */
    @Override
    public void addSupplierAccount(AdminSupplierDTO dto) {
        supplierMapper.addSupplier(dto);
        supplierContactMapper.addSupplierContact(dto);
    }
}
