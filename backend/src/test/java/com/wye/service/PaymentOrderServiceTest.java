package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wye.entity.BusFee;
import com.wye.entity.BusPaymentOrder;
import com.wye.mapper.BusFeeMapper;
import com.wye.mapper.BusPaymentOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentOrderServiceTest {

    private PaymentOrderService paymentOrderService;

    @Mock
    private BusFeeMapper busFeeMapper;

    @Mock
    private BusPaymentOrderMapper paymentOrderMapper;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        paymentOrderService = new PaymentOrderService();
        ReflectionTestUtils.setField(paymentOrderService, "busFeeMapper", busFeeMapper);
        ReflectionTestUtils.setField(paymentOrderService, "paymentOrderMapper", paymentOrderMapper);
        ReflectionTestUtils.setField(paymentOrderService, "notificationService", notificationService);
    }

    @Test
    void createOrderRejectsWrongOwner() {
        BusFee fee = new BusFee();
        fee.setId(7L);
        fee.setOwnerId(100L);
        fee.setStatus(0);
        fee.setAmount(new BigDecimal("88.00"));
        when(busFeeMapper.selectById(7L)).thenReturn(fee);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> paymentOrderService.createOrder(7L, 200L, "mock"));

        assertEquals("无权支付该账单", error.getMessage());
        verify(paymentOrderMapper, never()).insert(any(BusPaymentOrder.class));
    }

    @Test
    void createOrderReusesPendingOrder() {
        BusFee fee = new BusFee();
        fee.setId(7L);
        fee.setOwnerId(100L);
        fee.setStatus(0);
        fee.setAmount(new BigDecimal("88.00"));

        BusPaymentOrder existing = new BusPaymentOrder();
        existing.setOrderNo("PAY_EXISTING");
        existing.setFeeId(7L);
        existing.setOwnerId(100L);
        existing.setStatus(0);

        when(busFeeMapper.selectById(7L)).thenReturn(fee);
        when(paymentOrderMapper.selectOne(any(QueryWrapper.class))).thenReturn(existing);

        BusPaymentOrder order = paymentOrderService.createOrder(7L, 100L, "mock");

        assertEquals("PAY_EXISTING", order.getOrderNo());
        verify(paymentOrderMapper, never()).insert(any(BusPaymentOrder.class));
    }

    @Test
    void callbackIsIdempotentWhenOrderAlreadyPaid() {
        BusPaymentOrder paid = new BusPaymentOrder();
        paid.setOrderNo("PAY_DONE");
        paid.setStatus(1);
        paid.setOwnerId(100L);
        when(paymentOrderMapper.selectByOrderNo("PAY_DONE")).thenReturn(paid);

        Map<String, Object> result = paymentOrderService.handleCallback("PAY_DONE", "T123", true);

        assertEquals("SUCCESS", result.get("status"));
        assertTrue((Boolean) result.get("idempotent"));
        verify(paymentOrderMapper, never()).update(any(), any(UpdateWrapper.class));
        verify(busFeeMapper, never()).updateById(any(BusFee.class));
    }

    @Test
    void successfulCallbackPaysFeeAndNotifiesOwner() {
        BusPaymentOrder pending = new BusPaymentOrder();
        pending.setOrderNo("PAY_PENDING");
        pending.setFeeId(7L);
        pending.setOwnerId(100L);
        pending.setStatus(0);

        BusFee fee = new BusFee();
        fee.setId(7L);
        fee.setOwnerId(100L);
        fee.setStatus(0);
        fee.setMonth("2026-05");
        fee.setType("物业费");
        fee.setAmount(new BigDecimal("120.00"));

        when(paymentOrderMapper.selectByOrderNo("PAY_PENDING")).thenReturn(pending);
        when(paymentOrderMapper.selectByTradeNo("T123")).thenReturn(null);
        when(paymentOrderMapper.update(any(), any(UpdateWrapper.class))).thenReturn(1);
        when(busFeeMapper.selectById(7L)).thenReturn(fee);

        Map<String, Object> result = paymentOrderService.handleCallback("PAY_PENDING", "T123", true);

        assertEquals("SUCCESS", result.get("status"));
        assertEquals(false, result.get("idempotent"));
        verify(busFeeMapper).updateById(any(BusFee.class));
        verify(notificationService).create(eq(100L), eq("缴费成功"), any(String.class), eq("fee"));
    }
}
