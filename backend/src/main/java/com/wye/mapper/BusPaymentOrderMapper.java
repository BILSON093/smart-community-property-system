package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.BusPaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BusPaymentOrderMapper extends BaseMapper<BusPaymentOrder> {

    @Select("SELECT * FROM bus_payment_order WHERE order_no = #{orderNo} LIMIT 1")
    BusPaymentOrder selectByOrderNo(String orderNo);

    @Select("SELECT * FROM bus_payment_order WHERE trade_no = #{tradeNo} LIMIT 1")
    BusPaymentOrder selectByTradeNo(String tradeNo);
}
