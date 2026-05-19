package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.SysNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SysNotificationMapper extends BaseMapper<SysNotification> {

    @Select("SELECT COUNT(*) FROM sys_notification WHERE user_id = #{userId} AND is_read = 0")
    Long countUnread(Long userId);
}
