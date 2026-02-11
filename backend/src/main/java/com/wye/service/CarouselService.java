package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.entity.BusCarousel;
import com.wye.mapper.BusCarouselMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarouselService {
    
    @Autowired
    private BusCarouselMapper busCarouselMapper;
    
    /**
     * 查询展示中的轮播图列表
     */
    public List<BusCarousel> list() {
        return busCarouselMapper.selectList(
            new QueryWrapper<BusCarousel>()
                .eq("is_show", 1)
                .orderByAsc("sort_order")
        );
    }
    
    /**
     * 添加轮播图
     */
    public void add(BusCarousel carousel) {
        busCarouselMapper.insert(carousel);
    }
    
    /**
     * 更新轮播图
     */
    public void update(BusCarousel carousel) {
        busCarouselMapper.updateById(carousel);
    }
    
    /**
     * 删除轮播图
     */
    public void delete(Long id) {
        busCarouselMapper.deleteById(id);
    }
}
