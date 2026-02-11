package com.wye.controller;

import com.wye.common.Result;
import com.wye.entity.ChatRecord;
import com.wye.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息
     */
    @PostMapping("/send")
    public Result<String> send(@RequestBody ChatRecord record, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            // 使用用户ID作为sessionId
            chatService.sendMessage(userId, record.getSender(), record.getContent(), record.getMsgType());
            return Result.success("发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发送失败：" + e.getMessage());
        }
    }

    /**
     * 获取会话记录
     */
    @GetMapping("/session")
    public Result<List<Map<String, Object>>> getSession(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            return Result.success(chatService.getSession(userId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取管理员信息
     */
    @GetMapping("/admin/info")
    public Result<Map<String, Object>> getAdminInfo() {
        try {
            return Result.success(chatService.getAdminInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取管理员信息失败：" + e.getMessage());
        }
    }

    /**
     * 管理员获取所有会话列表
     */
    @GetMapping("/admin/sessions")
    public Result<List<Map<String, Object>>> getAllSessions() {
        try {
            return Result.success(chatService.getAllSessionsWithInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取失败：" + e.getMessage());
        }
    }

    /**
     * 管理员向指定会话发送消息
     */
    @PostMapping("/admin/reply")
    public Result<String> adminReply(@RequestBody ChatRecord record) {
        try {
            chatService.sendMessage(record.getSessionId(), 2, record.getContent(), record.getMsgType());
            return Result.success("回复成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("回复失败：" + e.getMessage());
        }
    }

    /**
     * 管理员获取指定会话的消息
     */
    @GetMapping("/admin/session")
    public Result<List<Map<String, Object>>> getSessionById(@RequestParam Long sessionId) {
        try {
            return Result.success(chatService.getSessionWithAvatar(sessionId));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取消息失败：" + e.getMessage());
        }
    }
}
