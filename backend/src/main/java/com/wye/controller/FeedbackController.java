package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.BusFeedback;
import com.wye.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    /**
     * 留言列表（管理员）
     */
    @RequireRole({0})
    @GetMapping("/list")
    public Result<Page<BusFeedback>> list(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) Integer status) {
        return Result.success(feedbackService.list(page, size, status));
    }
    
    /**
     * 我的留言列表（业主）
     */
    @RequireRole({1})
    @GetMapping("/my")
    public Result<java.util.List<BusFeedback>> myList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(feedbackService.listByUserId(userId.toString()));
    }
    
    /**
     * 留言详情
     */
    @GetMapping("/{id}")
    public Result<BusFeedback> getById(@PathVariable Long id) {
        return Result.success(feedbackService.getById(id));
    }
    
    /**
     * 添加留言
     */
    @RequireRole({1})
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusFeedback feedback) {
        feedbackService.add(feedback);
        return Result.success("添加成功");
    }
    
    /**
     * 更新留言（回复）
     */
    @RequireRole({0})
    @PutMapping("/update")
    public Result<String> update(@RequestBody BusFeedback feedback) {
        feedbackService.update(feedback);
        return Result.success("更新成功");
    }
    
    /**
     * 删除留言
     */
    @RequireRole({0})
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        feedbackService.delete(id);
        return Result.success("删除成功");
    }
}