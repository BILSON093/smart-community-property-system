package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.BusFee;
import com.wye.entity.BusFeeSettings;
import com.wye.entity.BusPaymentOrder;
import com.wye.service.FeeService;
import com.wye.service.FeeSettingsService;
import com.wye.service.PaymentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/fee")
public class FeeController {
    
    @Autowired
    private FeeService feeService;
    
    @Autowired
    private FeeSettingsService feeSettingsService;

    @Autowired
    private PaymentOrderService paymentOrderService;
    
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
    public Result<String> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            BusPaymentOrder order = paymentOrderService.createOrder(id, userId, "mock");
            paymentOrderService.simulateSuccess(order.getOrderNo());
            return Result.success("支付成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建模拟支付订单
     */
    @RequireRole({1})
    @PostMapping("/pay/order/{feeId}")
    public Result<?> createPaymentOrder(@PathVariable Long feeId,
                                        @RequestBody(required = false) Map<String, String> params,
                                        HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String channel = params != null ? params.get("channel") : "mock";
        try {
            return Result.success(paymentOrderService.createOrder(feeId, userId, channel));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 模拟第三方支付成功，用于本地演示支付状态流转
     */
    @RequireRole({1})
    @PostMapping("/pay/simulate/{orderNo}")
    public Result<?> simulatePay(@PathVariable String orderNo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        try {
            paymentOrderService.getByOrderNo(orderNo, userId, role);
            return Result.success(paymentOrderService.simulateSuccess(orderNo));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 支付回调。真实项目中由第三方支付平台调用；这里保留为模拟接口，内部做幂等处理。
     */
    @PostMapping("/pay/callback")
    public Result<?> paymentCallback(@RequestBody Map<String, Object> params) {
        String orderNo = String.valueOf(params.get("orderNo"));
        String tradeNo = params.get("tradeNo") == null ? null : String.valueOf(params.get("tradeNo"));
        boolean success = params.get("success") == null || Boolean.parseBoolean(String.valueOf(params.get("success")));
        try {
            return Result.success(paymentOrderService.handleCallback(orderNo, tradeNo, success));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询支付订单
     */
    @RequireRole({0, 1})
    @GetMapping("/pay/order/{orderNo}")
    public Result<BusPaymentOrder> getPaymentOrder(@PathVariable String orderNo, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer role = (Integer) request.getAttribute("role");
        try {
            BusPaymentOrder order = paymentOrderService.getByOrderNo(orderNo, userId, role);
            return Result.success(order);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
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
