package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.SysNotification;
import com.wye.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/my")
    public Result<Page<SysNotification>> myNotifications(HttpServletRequest request,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(notificationService.getMyNotifications(userId, page, size));
    }

    @PostMapping("/read/{id}")
    public Result<String> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success("已标记已读");
    }

    @PostMapping("/read-all")
    public Result<String> markAllAsRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.markAllAsRead(userId);
        return Result.success("全部已读");
    }

    @GetMapping("/unread-count")
    public Result<Map<String, Long>> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Long> result = new HashMap<>();
        result.put("count", notificationService.getUnreadCount(userId));
        return Result.success(result);
    }

    @RequireRole({0})
    @GetMapping("/list")
    public Result<Page<SysNotification>> listAll(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return Result.success(notificationService.listAll(page, size));
    }
}
