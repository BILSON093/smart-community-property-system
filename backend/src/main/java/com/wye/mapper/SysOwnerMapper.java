package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.SysOwner;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysOwnerMapper extends BaseMapper<SysOwner> {

    @Select("SELECT * FROM sys_owner WHERE user_id = #{userId}")
    SysOwner selectByUserId(Long userId);
}
