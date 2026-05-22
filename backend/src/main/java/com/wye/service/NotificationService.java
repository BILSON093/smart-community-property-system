package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.SysNotification;
import com.wye.mapper.SysNotificationMapper;
import com.wye.websocket.WebSocketPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private SysNotificationMapper notificationMapper;

    @Autowired
    private WebSocketPushService webSocketPushService;

    public void create(Long userId, String title, String content, String type) {
        SysNotification notification = new SysNotification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notification.setCreateTime(new Date());
        notificationMapper.insert(notification);

        Map<String, Object> payload = new HashMap<>();
        payload.put("id", notification.getId());
        payload.put("userId", userId);
        payload.put("title", title);
        payload.put("content", content);
        payload.put("type", type);
        payload.put("isRead", 0);
        payload.put("createTime", notification.getCreateTime());
        payload.put("unreadCount", getUnreadCount(userId));
        webSocketPushService.pushToUser(userId, "notification.created", payload);
        webSocketPushService.pushToAdmins("notification.created", payload);
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
            Map<String, Object> payload = new HashMap<>();
            payload.put("id", id);
            payload.put("unreadCount", getUnreadCount(notification.getUserId()));
            webSocketPushService.pushToUser(notification.getUserId(), "notification.read", payload);
        }
    }

    public void markAllAsRead(Long userId) {
        UpdateWrapper<SysNotification> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id", userId).eq("is_read", 0).set("is_read", 1);
        notificationMapper.update(null, wrapper);
        Map<String, Object> payload = new HashMap<>();
        payload.put("unreadCount", 0);
        webSocketPushService.pushToUser(userId, "notification.read_all", payload);
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
