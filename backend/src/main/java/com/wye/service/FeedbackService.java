package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusFeedback;
import com.wye.mapper.BusFeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    
    @Autowired
    private BusFeedbackMapper busFeedbackMapper;
    
    /**
     * 分页查询留言列表（管理员）
     */
    public Page<BusFeedback> list(int page, int size, Integer status) {
        QueryWrapper<BusFeedback> wrapper = new QueryWrapper<>();
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");
        return busFeedbackMapper.selectPage(new Page<>(page, size), wrapper);
    }
    
    /**
     * 根据用户ID查询留言列表（业主）
     */
    public List<BusFeedback> listByUserId(String userId) {
        QueryWrapper<BusFeedback> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return busFeedbackMapper.selectList(wrapper);
    }
    
    /**
     * 获取留言详情
     */
    public BusFeedback getById(Long id) {
        return busFeedbackMapper.selectById(id);
    }
    
    /**
     * 添加留言
     */
    public void add(BusFeedback feedback) {
        feedback.setStatus(0); // 默认状态为待处理
        busFeedbackMapper.insert(feedback);
    }
    
    /**
     * 更新留言（回复）
     */
    public void update(BusFeedback feedback) {
        feedback.setStatus(1); // 回复后状态为已处理
        busFeedbackMapper.updateById(feedback);
    }
    
    /**
     * 删除留言
     */
    public void delete(Long id) {
        busFeedbackMapper.deleteById(id);
    }
}