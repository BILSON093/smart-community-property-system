package com.wye.controller;

import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.dto.RegisterRequest;
import com.wye.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequireRole({0})
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前管理员信息
     */
    @GetMapping("/info")
    public Result<?> getAdminInfo(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            return userService.getUserById(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取管理员信息失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有业主列表
     */
    @GetMapping("/owners")
    public Result<?> getOwners() {
        return userService.getOwners();
    }

    /**
     * 获取所有维修员列表
     */
    @GetMapping("/workers")
    public Result<?> getWorkers() {
        return userService.getWorkers();
    }

    /**
     * 新增业主
     */
    @PostMapping("/owner/add")
    public Result<String> addOwner(@RequestBody RegisterRequest request) {
        return userService.registerOwner(request);
    }

    /**
     * 编辑业主
     */
    @PutMapping("/owner/update")
    public Result<String> updateOwner(@RequestBody RegisterRequest request) {
        return userService.updateOwner(request);
    }

    /**
     * 删除业主
     */
    @DeleteMapping("/owner/{id}")
    public Result<String> deleteOwner(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 新增维修员
     */
    @PostMapping("/worker/add")
    public Result<String> addWorker(@RequestBody RegisterRequest request) {
        return userService.registerWorker(request);
    }

    /**
     * 编辑维修员
     */
    @PutMapping("/worker/update")
    public Result<String> updateWorker(@RequestBody RegisterRequest request) {
        return userService.updateWorker(request);
    }

    /**
     * 审核维修员
     */
    @PutMapping("/worker/approve")
    public Result<String> approveWorker(@RequestParam Long workerId, @RequestParam Integer status) {
        return userService.approveWorker(workerId, status);
    }

    /**
     * 删除维修员
     */
    @DeleteMapping("/worker/{id}")
    public Result<String> deleteWorker(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        try {
            Map<String, Object> stats = userService.getStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有管理员列表
     */
    @GetMapping("/list")
    public Result<?> getAdmins() {
        return userService.getAdmins();
    }

    /**
     * 添加管理员
     */
    @PostMapping("/add")
    public Result<String> addAdmin(@RequestBody RegisterRequest request) {
        return userService.addAdmin(request);
    }

    /**
     * 更新管理员信息
     */
    @PutMapping("/update")
    public Result<String> updateAdmin(@RequestBody RegisterRequest request) {
        return userService.updateAdmin(request);
    }

    /**
     * 删除管理员
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteAdmin(@PathVariable Long id) {
        return userService.deleteAdmin(id);
    }
}
