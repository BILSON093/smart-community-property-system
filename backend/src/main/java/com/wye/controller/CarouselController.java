package com.wye.controller;

import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.BusCarousel;
import com.wye.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carousel")
public class CarouselController {
    
    @Autowired
    private CarouselService carouselService;
    
    /**
     * 轮播图列表
     */
    @GetMapping("/list")
    public Result<List<BusCarousel>> list() {
        return Result.success(carouselService.list());
    }
    
    /**
     * 添加轮播图
     */
    @RequireRole({0})
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusCarousel carousel) {
        carouselService.add(carousel);
        return Result.success("添加成功");
    }
    
    /**
     * 更新轮播图
     */
    @RequireRole({0})
    @PutMapping("/update")
    public Result<String> update(@RequestBody BusCarousel carousel) {
        carouselService.update(carousel);
        return Result.success("更新成功");
    }
    
    /**
     * 删除轮播图
     */
    @RequireRole({0})
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        carouselService.delete(id);
        return Result.success("删除成功");
    }
}
