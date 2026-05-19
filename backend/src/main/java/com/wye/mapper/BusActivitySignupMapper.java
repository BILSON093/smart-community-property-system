package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.BusActivitySignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BusActivitySignupMapper extends BaseMapper<BusActivitySignup> {

    @Select("SELECT COUNT(*) FROM bus_activity_signup WHERE activity_id = #{activityId}")
    Long countByActivityId(Long activityId);
}
