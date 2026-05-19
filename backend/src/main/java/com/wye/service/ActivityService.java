package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusActivity;
import com.wye.entity.BusActivitySignup;
import com.wye.mapper.BusActivityMapper;
import com.wye.mapper.BusActivitySignupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private BusActivityMapper busActivityMapper;

    @Autowired
    private BusActivitySignupMapper busActivitySignupMapper;
    
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

    public void signup(Long activityId, Long userId) {
        if (isSignedUp(activityId, userId)) {
            throw new RuntimeException("已报名该活动");
        }
        BusActivitySignup signup = new BusActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setCreateTime(new Date());
        busActivitySignupMapper.insert(signup);
    }

    public void cancelSignup(Long activityId, Long userId) {
        busActivitySignupMapper.delete(new QueryWrapper<BusActivitySignup>()
            .eq("activity_id", activityId)
            .eq("user_id", userId)
        );
    }

    public boolean isSignedUp(Long activityId, Long userId) {
        return busActivitySignupMapper.selectCount(new QueryWrapper<BusActivitySignup>()
            .eq("activity_id", activityId)
            .eq("user_id", userId)
        ) > 0;
    }

    public Long getSignupCount(Long activityId) {
        return busActivitySignupMapper.countByActivityId(activityId);
    }

    public List<BusActivitySignup> getSignups(Long activityId) {
        return busActivitySignupMapper.selectList(new QueryWrapper<BusActivitySignup>()
            .eq("activity_id", activityId)
            .orderByDesc("create_time")
        );
    }
}
