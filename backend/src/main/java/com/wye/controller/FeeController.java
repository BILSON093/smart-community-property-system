package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.BusFee;
import com.wye.entity.BusFeeSettings;
import com.wye.service.FeeService;
import com.wye.service.FeeSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/fee")
public class FeeController {
    
    @Autowired
    private FeeService feeService;
    
    @Autowired
    private FeeSettingsService feeSettingsService;
    
    /**
     * 缴费列表（管理员）
     */
    @RequireRole({0})
    @GetMapping("/list")
    public Result<Page<BusFee>> list(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam(required = false) String month,
                                     @RequestParam(required = false) String status,
                                     @RequestParam(required = false) String type,
                                     @RequestParam(required = false) String keyword) {
        return Result.success(feeService.list(page, size, month, status, type, keyword));
    }
    
    /**
     * 我的缴费列表（业主）
     */
    @RequireRole({1})
    @GetMapping("/my")
    public Result<Page<BusFee>> myFees(HttpServletRequest request,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(feeService.listByOwnerId(userId, page, size));
    }
    
    /**
     * 添加缴费记录
     */
    @RequireRole({0})
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusFee fee) {
        feeService.add(fee);
        return Result.success("添加成功");
    }
    
    /**
     * 支付
     */
    @RequireRole({1})
    @PostMapping("/pay/{id}")
    public Result<String> pay(@PathVariable Long id) {
        feeService.pay(id);
        return Result.success("支付成功");
    }
    
    /**
     * 删除缴费记录
     */
    @RequireRole({0})
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        feeService.delete(id);
        return Result.success("删除成功");
    }
    
    /**
     * 获取费用单价设置
     */
    @GetMapping("/settings")
    public Result<BusFeeSettings> getSettings() {
        return Result.success(feeSettingsService.getSettings());
    }
    
    /**
     * 保存费用单价设置
     */
    @RequireRole({0})
    @PostMapping("/settings")
    public Result<String> saveSettings(@RequestBody BusFeeSettings settings) {
        feeSettingsService.saveSettings(settings);
        return Result.success("设置保存成功");
    }
    
    /**
     * 一键催缴
     */
    @RequireRole({0})
    @PostMapping("/urge-payment")
    public Result<String> urgePayment() {
        feeService.urgePayment();
        return Result.success("一键催缴成功");
    }
}
