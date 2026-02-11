package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.Result;
import com.wye.entity.BusNotice;
import com.wye.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusNotice notice) {
        noticeService.add(notice);
        return Result.success("添加成功");
    }
    
    /**
     * 更新通知
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody BusNotice notice) {
        noticeService.update(notice);
        return Result.success("更新成功");
    }
    
    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Result.success("删除成功");
    }
}
