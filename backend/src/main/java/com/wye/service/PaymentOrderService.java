package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wye.entity.BusFee;
import com.wye.entity.BusPaymentOrder;
import com.wye.mapper.BusFeeMapper;
import com.wye.mapper.BusPaymentOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentOrderService {

    @Autowired
    private BusFeeMapper busFeeMapper;

    @Autowired
    private BusPaymentOrderMapper paymentOrderMapper;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public BusPaymentOrder createOrder(Long feeId, Long ownerId, String channel) {
        BusFee fee = busFeeMapper.selectById(feeId);
        if (fee == null) {
            throw new IllegalArgumentException("账单不存在");
        }
        if (!fee.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("无权支付该账单");
        }
        if (fee.getStatus() != null && fee.getStatus() == 1) {
            throw new IllegalArgumentException("账单已缴费");
        }

        BusPaymentOrder existing = paymentOrderMapper.selectOne(new QueryWrapper<BusPaymentOrder>()
            .eq("fee_id", feeId)
            .eq("owner_id", ownerId)
            .eq("status", 0)
            .orderByDesc("create_time")
            .last("LIMIT 1"));
        if (existing != null) {
            return existing;
        }

        Date now = new Date();
        BusPaymentOrder order = new BusPaymentOrder();
        order.setOrderNo(generateOrderNo());
        order.setFeeId(feeId);
        order.setOwnerId(ownerId);
        order.setAmount(fee.getAmount());
        order.setStatus(0);
        order.setChannel(channel == null || channel.isEmpty() ? "mock" : channel);
        order.setCreateTime(now);
        order.setUpdateTime(now);
        paymentOrderMapper.insert(order);
        return order;
    }

    @Transactional
    public Map<String, Object> simulateSuccess(String orderNo) {
        String tradeNo = "MOCK" + System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return handleCallback(orderNo, tradeNo, true);
    }

    @Transactional
    public Map<String, Object> handleCallback(String orderNo, String tradeNo, boolean success) {
        BusPaymentOrder order = paymentOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new IllegalArgumentException("支付订单不存在");
        }

        if (tradeNo != null && !tradeNo.isEmpty()) {
            BusPaymentOrder tradeOrder = paymentOrderMapper.selectByTradeNo(tradeNo);
            if (tradeOrder != null && !tradeOrder.getOrderNo().equals(orderNo)) {
                throw new IllegalArgumentException("第三方流水号已被其他订单使用");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", orderNo);

        if (order.getStatus() != null && order.getStatus() == 1) {
            result.put("status", "SUCCESS");
            result.put("idempotent", true);
            result.put("message", "订单已支付，重复回调已忽略");
            return result;
        }

        if (!success) {
            order.setStatus(2);
            order.setTradeNo(tradeNo);
            order.setUpdateTime(new Date());
            paymentOrderMapper.updateById(order);
            result.put("status", "CLOSED");
            result.put("idempotent", false);
            result.put("message", "支付失败，订单已关闭");
            return result;
        }

        Date now = new Date();
        UpdateWrapper<BusPaymentOrder> orderUpdate = new UpdateWrapper<>();
        orderUpdate.eq("order_no", orderNo)
            .eq("status", 0)
            .set("status", 1)
            .set("trade_no", tradeNo)
            .set("paid_time", now)
            .set("update_time", now);
        int updated = paymentOrderMapper.update(null, orderUpdate);
        if (updated == 0) {
            result.put("status", "SUCCESS");
            result.put("idempotent", true);
            result.put("message", "订单状态已处理");
            return result;
        }

        BusFee fee = busFeeMapper.selectById(order.getFeeId());
        if (fee != null && (fee.getStatus() == null || fee.getStatus() == 0)) {
            fee.setStatus(1);
            fee.setReminded(0);
            fee.setPayTime(now);
            busFeeMapper.updateById(fee);
            notificationService.create(order.getOwnerId(), "缴费成功",
                "您已成功缴纳" + fee.getMonth() + " " + fee.getType() + "，金额 " + fee.getAmount() + " 元", "fee");
        }

        result.put("status", "SUCCESS");
        result.put("idempotent", false);
        result.put("message", "支付成功");
        return result;
    }

    public BusPaymentOrder getByOrderNo(String orderNo, Long ownerId, Integer role) {
        BusPaymentOrder order = paymentOrderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            return null;
        }
        if (role == null || role != 0) {
            if (!order.getOwnerId().equals(ownerId)) {
                throw new IllegalArgumentException("无权查看该支付订单");
            }
        }
        return order;
    }

    private String generateOrderNo() {
        String time = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "PAY" + time + suffix;
    }
}
