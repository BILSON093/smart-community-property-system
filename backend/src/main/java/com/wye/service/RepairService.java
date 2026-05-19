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

import java.util.*;

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

    @Autowired
    private NotificationService notificationService;
    
    /**
     * 分页查询报修列表
     */
    public Page<BusRepair> list(int page, int size, Integer status) {
        return list(page, size, status, null);
    }

    public Page<BusRepair> list(int page, int size, Integer status, String type) {
        QueryWrapper<BusRepair> wrapper = new QueryWrapper<BusRepair>().orderByDesc("create_time");
        if (status != null) {
            wrapper.eq("status", status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        Page<BusRepair> result = busRepairMapper.selectPage(new Page<>(page, size), wrapper);
        result.getRecords().forEach(this::addRepairDetails);
        return result;
    }
    
    /**
     * 查询业主的报修列表
     */
    public Page<BusRepair> listByOwnerId(Long ownerId, int page, int size) {
        return listByOwnerId(ownerId, page, size, null);
    }

    public Page<BusRepair> listByOwnerId(Long ownerId, int page, int size, String type) {
        QueryWrapper<BusRepair> wrapper = new QueryWrapper<BusRepair>()
            .eq("owner_id", ownerId)
            .orderByDesc("create_time");
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        Page<BusRepair> result = busRepairMapper.selectPage(new Page<>(page, size), wrapper);
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
            notificationService.create(repair.getOwnerId(), "报修已派单",
                "您的报修工单已派单，维修员即将上门服务", "repair");
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
            repair.setCompleteTime(new java.util.Date());
            busRepairMapper.updateById(repair);
            notificationService.create(repair.getOwnerId(), "维修已完成",
                "您的报修工单已完成，请对服务进行评价", "repair");
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
            repair.setStatus(0);
            repair.setWorkerId(null);
            busRepairMapper.updateById(repair);
        }
    }

    public Map<String, Object> getWorkerStats(Long workerId) {
        Map<String, Object> stats = new HashMap<>();
        List<BusRepair> completed = busRepairMapper.selectList(new QueryWrapper<BusRepair>()
            .eq("worker_id", workerId)
            .eq("status", 3)
        );
        stats.put("completedCount", completed.size());

        double avgScore = 0;
        if (!completed.isEmpty()) {
            List<Long> repairIds = new ArrayList<>();
            for (BusRepair r : completed) {
                repairIds.add(r.getId());
            }
            List<BusEvaluation> evaluations = busEvaluationMapper.selectList(
                new QueryWrapper<BusEvaluation>().in("repair_id", repairIds)
            );
            if (!evaluations.isEmpty()) {
                int totalScore = 0;
                for (BusEvaluation e : evaluations) {
                    totalScore += e.getScore();
                }
                avgScore = (double) totalScore / evaluations.size();
            }

            long totalMinutes = 0;
            int countWithTime = 0;
            for (BusRepair r : completed) {
                if (r.getCreateTime() != null && r.getCompleteTime() != null) {
                    long diff = r.getCompleteTime().getTime() - r.getCreateTime().getTime();
                    totalMinutes += diff / (1000 * 60);
                    countWithTime++;
                }
            }
            stats.put("avgMinutes", countWithTime > 0 ? totalMinutes / countWithTime : 0);
        } else {
            stats.put("avgMinutes", 0);
        }
        stats.put("avgScore", Math.round(avgScore * 10) / 10.0);
        return stats;
    }
}
