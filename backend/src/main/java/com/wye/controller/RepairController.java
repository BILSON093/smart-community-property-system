package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.Result;
import com.wye.entity.BusRepair;
import com.wye.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/repair")
public class RepairController {
    
    @Autowired
    private RepairService repairService;
    
    /**
     * 报修列表（管理员）
     */
    @GetMapping("/list")
    public Result<Page<BusRepair>> list(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(required = false) Integer status) {
        return Result.success(repairService.list(page, size, status));
    }
    
    /**
     * 我的报修（业主）
     */
    @GetMapping("/my")
    public Result<Page<BusRepair>> myRepairs(HttpServletRequest request,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(repairService.list(page, size, null));
    }
    
    /**
     * 我的工单（维修员）
     */
    @GetMapping("/worker")
    public Result<Page<BusRepair>> workerRepairs(HttpServletRequest request,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(repairService.listByWorkerId(userId, page, size));
    }
    
    /**
     * 我的已完成订单（维修员）
     */
    @GetMapping("/worker/completed")
    public Result<Page<BusRepair>> workerCompletedRepairs(HttpServletRequest request,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(repairService.listCompletedByWorkerId(userId, page, size));
    }
    
    /**
     * 报修详情
     */
    @GetMapping("/{id}")
    public Result<BusRepair> getById(@PathVariable Long id) {
        return Result.success(repairService.getById(id));
    }
    
    /**
     * 提交报修
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusRepair repair, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        repair.setOwnerId(userId);
        repairService.add(repair);
        return Result.success("报修成功");
    }
    
    /**
     * 派单
     */
    @PostMapping("/dispatch")
    public Result<String> dispatch(@RequestBody Map<String, Long> params) {
        repairService.dispatch(params.get("repairId"), params.get("workerId"));
        return Result.success("派单成功");
    }
    
    /**
     * 开始维修
     */
    @PostMapping("/start/{repairId}")
    public Result<String> startRepair(@PathVariable Long repairId) {
        repairService.startRepair(repairId);
        return Result.success("开始维修");
    }
    
    /**
     * 完成维修
     */
    @PostMapping("/complete/{repairId}")
    public Result<String> completeRepair(@PathVariable Long repairId) {
        repairService.completeRepair(repairId);
        return Result.success("维修完成");
    }
    
    /**
     * 删除报修
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        repairService.delete(id);
        return Result.success("删除成功");
    }
    
    /**
     * 评价维修服务
     */
    @PostMapping("/evaluate")
    public Result<String> evaluate(@RequestBody com.wye.entity.BusEvaluation evaluation) {
        repairService.evaluate(evaluation);
        return Result.success("评价成功");
    }
    
    /**
     * 退单（维修员取消维修）
     */
    @PostMapping("/cancel/{repairId}")
    public Result<String> cancelRepair(@PathVariable Long repairId) {
        repairService.cancelRepair(repairId);
        return Result.success("退单成功");
    }
}
