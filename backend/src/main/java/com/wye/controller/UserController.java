package com.wye.controller;

import com.wye.common.Result;
import com.wye.dto.LoginRequest;
import com.wye.dto.RegisterRequest;
import com.wye.entity.SysUser;
import com.wye.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
    
    /**
     * 维修员注册
     */
    @PostMapping("/register/worker")
    public Result<String> registerWorker(@RequestBody RegisterRequest request) {
        return userService.registerWorker(request);
    }

    /**
     * 业主注册
     */
    @PostMapping("/register/owner")
    public Result<String> registerOwner(@RequestBody RegisterRequest request) {
        return userService.registerOwner(request);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return userService.getUserById(userId);
    }
    
    /**
     * 业主注册（兼容旧路径）
     */
    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request) {
        return userService.registerOwner(request);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody RegisterRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        request.setUserId(userId);
        return userService.updateUser(request);
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        return userService.changePassword(userId, request.get("oldPassword"), request.get("newPassword"));
    }
}
