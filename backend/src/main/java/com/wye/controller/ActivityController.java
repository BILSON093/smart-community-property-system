package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.BusActivity;
import com.wye.entity.BusActivitySignup;
import com.wye.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;
    
    /**
     * 活动列表
     */
    @GetMapping("/list")
    public Result<Page<BusActivity>> list(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return Result.success(activityService.list(page, size));
    }
    
    /**
     * 活动详情
     */
    @GetMapping("/{id}")
    public Result<BusActivity> getById(@PathVariable Long id) {
        return Result.success(activityService.getById(id));
    }
    
    /**
     * 添加活动
     */
    @RequireRole({0})
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusActivity activity) {
        activityService.add(activity);
        return Result.success("添加成功");
    }
    
    /**
     * 更新活动
     */
    @RequireRole({0})
    @PutMapping("/update")
    public Result<String> update(@RequestBody BusActivity activity) {
        activityService.update(activity);
        return Result.success("更新成功");
    }
    
    /**
     * 删除活动
     */
    @RequireRole({0})
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/signup")
    public Result<String> signup(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activityService.signup(id, userId);
        return Result.success("报名成功");
    }

    @DeleteMapping("/{id}/signup")
    public Result<String> cancelSignup(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activityService.cancelSignup(id, userId);
        return Result.success("取消报名成功");
    }

    @GetMapping("/{id}/signup-status")
    public Result<Map<String, Object>> signupStatus(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = new HashMap<>();
        result.put("signedUp", activityService.isSignedUp(id, userId));
        result.put("count", activityService.getSignupCount(id));
        return Result.success(result);
    }

    @RequireRole({0})
    @GetMapping("/{id}/signups")
    public Result<List<BusActivitySignup>> getSignups(@PathVariable Long id) {
        return Result.success(activityService.getSignups(id));
    }
}
