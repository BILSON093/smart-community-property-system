package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusActivity;
import com.wye.mapper.BusActivityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    
    @Autowired
    private BusActivityMapper busActivityMapper;
    
    /**
     * 分页查询活动列表
     */
    public Page<BusActivity> list(int page, int size) {
        return busActivityMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusActivity>().orderByDesc("create_time")
        );
    }
    
    /**
     * 获取活动详情
     */
    public BusActivity getById(Long id) {
        return busActivityMapper.selectById(id);
    }
    
    /**
     * 添加活动
     */
    public void add(BusActivity activity) {
        busActivityMapper.insert(activity);
    }
    
    /**
     * 更新活动
     */
    public void update(BusActivity activity) {
        busActivityMapper.updateById(activity);
    }
    
    /**
     * 删除活动
     */
    public void delete(Long id) {
        busActivityMapper.deleteById(id);
    }
}
