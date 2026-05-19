package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.SysNotification;
import com.wye.mapper.SysNotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NotificationService {

    @Autowired
    private SysNotificationMapper notificationMapper;

    public void create(Long userId, String title, String content, String type) {
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setCreateTime(new Date());
        notificationMapper.insert(notification);
    }

    public Page<SysNotification> getMyNotifications(Long userId, int page, int size) {
        return notificationMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<SysNotification>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
        );
    }

    public void markAsRead(Long id) {
        SysNotification notification = notificationMapper.selectById(id);
        if (notification != null) {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        }
    }

    public void markAllAsRead(Long userId) {
        UpdateWrapper<SysNotification> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId).eq("is_read", 0).set("is_read", 1);
        notificationMapper.update(null, wrapper);
    }

    public Long getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    public Page<SysNotification> listAll(int page, int size) {
        return notificationMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<SysNotification>().orderByDesc("create_time")
        );
    }
}
