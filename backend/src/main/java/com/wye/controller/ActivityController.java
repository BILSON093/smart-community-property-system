package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.Result;
import com.wye.entity.BusActivity;
import com.wye.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusActivity activity) {
        activityService.add(activity);
        return Result.success("添加成功");
    }
    
    /**
     * 更新活动
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody BusActivity activity) {
        activityService.update(activity);
        return Result.success("更新成功");
    }
    
    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        activityService.delete(id);
        return Result.success("删除成功");
    }
}
