package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.BusNoticeRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BusNoticeReadMapper extends BaseMapper<BusNoticeRead> {

    @Select("SELECT COUNT(*) FROM bus_notice_read WHERE notice_id = #{noticeId}")
    Long countByNoticeId(Long noticeId);
}
