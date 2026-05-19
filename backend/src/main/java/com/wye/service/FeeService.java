package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusFee;
import com.wye.entity.BusNotice;
import com.wye.entity.SysOwner;
import com.wye.entity.SysUser;
import com.wye.mapper.BusFeeMapper;
import com.wye.mapper.BusNoticeMapper;
import com.wye.mapper.SysOwnerMapper;
import com.wye.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeService {
    
    @Autowired
    private BusFeeMapper busFeeMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysOwnerMapper sysOwnerMapper;
    
    @Autowired
    private BusNoticeMapper busNoticeMapper;

    @Autowired
    private NotificationService notificationService;
    
    /**
     * 分页查询缴费列表
     */
    public Page<BusFee> list(int page, int size, String month, String status, String type, String keyword) {
        QueryWrapper<BusFee> wrapper = new QueryWrapper<BusFee>().orderByDesc("month");
        
        // 添加筛选条件
        if (month != null && !month.isEmpty()) {
            wrapper.eq("month", month);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        
        // 处理关键词搜索
        if (keyword != null && !keyword.isEmpty()) {
            // 先查询符合条件的业主
            List<Long> ownerIds = new ArrayList<>();
            
            // 查询用户表，根据姓名或电话搜索
            QueryWrapper<SysUser> userWrapper = new QueryWrapper<>();
            userWrapper.like("real_name", keyword).or().like("phone", keyword);
            List<SysUser> users = sysUserMapper.selectList(userWrapper);
            users.forEach(user -> ownerIds.add(user.getId()));
            
            // 查询业主表，根据楼栋、单元或房间号搜索
            QueryWrapper<SysOwner> ownerWrapper = new QueryWrapper<>();
            ownerWrapper.like("building", keyword).or().like("unit", keyword).or().like("room", keyword);
            List<SysOwner> owners = sysOwnerMapper.selectList(ownerWrapper);
            owners.forEach(owner -> ownerIds.add(owner.getUserId()));
            
            // 如果有符合条件的业主，添加到查询条件中
            if (!ownerIds.isEmpty()) {
                wrapper.in("owner_id", ownerIds);
            } else {
                // 如果没有符合条件的业主，返回空结果
                return new Page<>(page, size);
            }
        }
        
        Page<BusFee> result = busFeeMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 为每个缴费对象添加业主姓名和住址信息
        result.getRecords().forEach(fee -> {
            if (fee.getOwnerId() != null) {
                SysUser owner = sysUserMapper.selectById(fee.getOwnerId());
                if (owner != null) {
                    fee.setOwnerName(owner.getRealName());
                }
                
                // 添加业主的楼栋、单元和房间号
                SysOwner sysOwner = sysOwnerMapper.selectByUserId(fee.getOwnerId());
                if (sysOwner != null) {
                    fee.setBuilding(sysOwner.getBuilding());
                    fee.setUnit(sysOwner.getUnit());
                    fee.setRoom(sysOwner.getRoom());
                }
            }
        });
        
        return result;
    }
    
    /**
     * 查询业主的缴费列表
     */
    public Page<BusFee> listByOwnerId(Long ownerId, int page, int size) {
        return busFeeMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusFee>().eq("owner_id", ownerId).orderByDesc("month")
        );
    }
    
    /**
     * 添加缴费记录
     */
    public void add(BusFee fee) {
        busFeeMapper.insert(fee);
    }
    
    /**
     * 支付
     */
    public void pay(Long id) {
        BusFee fee = busFeeMapper.selectById(id);
        if (fee != null) {
            if (fee.getStatus() != null && fee.getStatus() == 1) {
                return; // 已缴费，不重复处理
            }
            fee.setStatus(1);
            fee.setReminded(0); // 缴费后重置提醒状态
            fee.setPayTime(new Date());
            busFeeMapper.updateById(fee);
        }
    }
    
    /**
     * 删除缴费记录
     */
    public void delete(Long id) {
        busFeeMapper.deleteById(id);
    }
    
    /**
     * 一键催缴
     */
    public void urgePayment() {
        // 查询所有未缴费且未提醒的费用记录
        QueryWrapper<BusFee> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0); // 0表示未缴费
        wrapper.eq("reminded", 0); // 0表示未提醒
        List<BusFee> unpaidFees = busFeeMapper.selectList(wrapper);
        
        // 为每个未缴费记录标记为已提醒
        for (BusFee fee : unpaidFees) {
            fee.setReminded(1); // 1表示已提醒
            busFeeMapper.updateById(fee);
        }
        
        // 按照业主ID分组
        Map<Long, List<BusFee>> feesByOwner = new HashMap<>();
        for (BusFee fee : unpaidFees) {
            feesByOwner.computeIfAbsent(fee.getOwnerId(), k -> new ArrayList<>()).add(fee);
        }
        
        for (Map.Entry<Long, List<BusFee>> entry : feesByOwner.entrySet()) {
            Long ownerId = entry.getKey();
            List<BusFee> fees = entry.getValue();

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (BusFee fee : fees) {
                totalAmount = totalAmount.add(fee.getAmount());
            }

            notificationService.create(ownerId, "缴费提醒",
                "您有 " + fees.size() + " 笔费用未缴，合计 " + totalAmount + " 元，请及时缴纳", "fee");
        }
    }
}
