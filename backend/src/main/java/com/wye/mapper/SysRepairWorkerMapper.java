package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.SysRepairWorker;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysRepairWorkerMapper extends BaseMapper<SysRepairWorker> {

    @Select("SELECT * FROM sys_repair_worker WHERE user_id = #{userId}")
    SysRepairWorker selectByUserId(Long userId);
}
