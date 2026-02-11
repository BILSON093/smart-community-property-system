package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusEvaluation;
import com.wye.entity.BusRepair;
import com.wye.entity.SysOwner;
import com.wye.entity.SysUser;
import com.wye.mapper.BusEvaluationMapper;
import com.wye.mapper.BusRepairMapper;
import com.wye.mapper.SysOwnerMapper;
import com.wye.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairService {
    
    @Autowired
    private BusRepairMapper busRepairMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private SysOwnerMapper sysOwnerMapper;
    
    @Autowired
    private BusEvaluationMapper busEvaluationMapper;
    
    /**
     * 分页查询报修列表
     */
    public Page<BusRepair> list(int page, int size, Integer status) {
        QueryWrapper<BusRepair> wrapper = new QueryWrapper<BusRepair>().orderByDesc("create_time");
        if (status != null) {
            wrapper.eq("status", status);
        }
        Page<BusRepair> result = busRepairMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 为每个报修对象添加详细信息
        result.getRecords().forEach(this::addRepairDetails);
        
        return result;
    }
    
    /**
     * 查询维修员的工单
     */
    public Page<BusRepair> listByWorkerId(Long workerId, int page, int size) {
        Page<BusRepair> result = busRepairMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusRepair>()
                .eq("worker_id", workerId)
                .orderByDesc("create_time")
        );
        
        // 为每个工单添加详细信息
        result.getRecords().forEach(this::addRepairDetails);
        
        return result;
    }
    
    /**
     * 查询维修员的已完成订单
     */
    public Page<BusRepair> listCompletedByWorkerId(Long workerId, int page, int size) {
        Page<BusRepair> result = busRepairMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusRepair>()
                .eq("worker_id", workerId)
                .eq("status", 3) // 3表示已完成
                .orderByDesc("complete_time")
        );
        
        // 为每个订单添加详细信息
        result.getRecords().forEach(this::addRepairDetails);
        
        return result;
    }
    
    /**
     * 为报修对象添加详细信息
     */
    private void addRepairDetails(BusRepair repair) {
        if (repair.getOwnerId() != null) {
            SysUser owner = sysUserMapper.selectById(repair.getOwnerId());
            if (owner != null) {
                repair.setOwnerName(owner.getRealName());
                repair.setPhone(owner.getPhone());
            }
            
            // 添加业主的楼栋、单元和房间号
            SysOwner sysOwner = sysOwnerMapper.selectByUserId(repair.getOwnerId());
            if (sysOwner != null) {
                repair.setBuilding(sysOwner.getBuilding());
                repair.setUnit(sysOwner.getUnit());
                repair.setRoom(sysOwner.getRoom());
            }
        }
        if (repair.getWorkerId() != null) {
            SysUser worker = sysUserMapper.selectById(repair.getWorkerId());
            if (worker != null) {
                repair.setWorkerName(worker.getRealName());
            }
        }
        
        // 添加评价信息
        BusEvaluation evaluation = busEvaluationMapper.selectOne(new QueryWrapper<BusEvaluation>()
            .eq("repair_id", repair.getId()));
        if (evaluation != null) {
            repair.setEvaluation(evaluation);
        }
    }
    
    /**
     * 添加报修
     */
    public void add(BusRepair repair) {
        busRepairMapper.insert(repair);
    }
    
    /**
     * 派单
     */
    public void dispatch(Long repairId, Long workerId) {
        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair != null) {
            repair.setStatus(1);
            repair.setWorkerId(workerId);
            busRepairMapper.updateById(repair);
        }
    }
    
    /**
     * 维修员开始维修
     */
    public void startRepair(Long repairId) {
        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair != null) {
            repair.setStatus(2);
            busRepairMapper.updateById(repair);
        }
    }
    
    /**
     * 完成维修
     */
    public void completeRepair(Long repairId) {
        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair != null) {
            repair.setStatus(3);
            repair.setCompleteTime(new java.util.Date()); // 设置完成时间
            busRepairMapper.updateById(repair);
        }
    }
    
    /**
     * 获取报修详情
     */
    public BusRepair getById(Long id) {
        BusRepair repair = busRepairMapper.selectById(id);
        if (repair != null) {
            // 添加详细信息
            addRepairDetails(repair);
        }
        return repair;
    }
    
    /**
     * 删除报修
     */
    public void delete(Long id) {
        busRepairMapper.deleteById(id);
    }
    
    /**
     * 评价维修服务
     */
    public void evaluate(com.wye.entity.BusEvaluation evaluation) {
        evaluation.setCreateTime(new java.util.Date());
        busEvaluationMapper.insert(evaluation);
    }
    
    /**
     * 退单（维修员取消维修）
     */
    public void cancelRepair(Long repairId) {
        BusRepair repair = busRepairMapper.selectById(repairId);
        if (repair != null) {
            repair.setStatus(0); // 重置为待审核状态
            repair.setWorkerId(null); // 清空维修员ID
            busRepairMapper.updateById(repair);
        }
    }
}
