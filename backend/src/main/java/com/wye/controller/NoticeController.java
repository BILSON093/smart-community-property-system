package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.BusNotice;
import com.wye.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;
    
    /**
     * 通知列表
     */
    @GetMapping("/list")
    public Result<Page<BusNotice>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return Result.success(noticeService.list(page, size));
    }
    
    /**
     * 通知详情
     */
    @GetMapping("/{id}")
    public Result<BusNotice> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }
    
    /**
     * 添加通知
     */
    @RequireRole({0})
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusNotice notice) {
        noticeService.add(notice);
        return Result.success("添加成功");
    }
    
    /**
     * 更新通知
     */
    @RequireRole({0})
    @PutMapping("/update")
    public Result<String> update(@RequestBody BusNotice notice) {
        noticeService.update(notice);
        return Result.success("更新成功");
    }
    
    /**
     * 删除通知
     */
    @RequireRole({0})
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/read")
    public Result<String> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        noticeService.markAsRead(id, userId);
        return Result.success("已读");
    }

    @GetMapping("/{id}/read-status")
    public Result<Map<String, Object>> readStatus(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Map<String, Object> result = new HashMap<>();
        result.put("isRead", noticeService.isRead(id, userId));
        result.put("readCount", noticeService.getReadCount(id));
        return Result.success(result);
    }
}
