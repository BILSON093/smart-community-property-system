package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.BusFee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusFeeMapper extends BaseMapper<BusFee> {
    
    @Select("SELECT * FROM bus_fee WHERE owner_id = #{userId} AND status = 0")
    List<BusFee> selectUnpaid(Long userId);
}
