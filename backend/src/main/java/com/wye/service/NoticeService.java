package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusNotice;
import com.wye.entity.BusNoticeRead;
import com.wye.mapper.BusNoticeMapper;
import com.wye.mapper.BusNoticeReadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class NoticeService {

    @Autowired
    private BusNoticeMapper busNoticeMapper;

    @Autowired
    private BusNoticeReadMapper busNoticeReadMapper;
    
    /**
     * 分页查询通知列表
     */
    public Page<BusNotice> list(int page, int size) {
        return busNoticeMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusNotice>().orderByDesc("publish_time")
        );
    }
    
    /**
     * 获取通知详情
     */
    public BusNotice getById(Long id) {
        return busNoticeMapper.selectById(id);
    }
    
    /**
     * 添加通知
     */
    public void add(BusNotice notice) {
        busNoticeMapper.insert(notice);
    }
    
    /**
     * 更新通知
     */
    public void update(BusNotice notice) {
        busNoticeMapper.updateById(notice);
    }
    
    /**
     * 删除通知
     */
    public void delete(Long id) {
        busNoticeMapper.deleteById(id);
    }

    public void markAsRead(Long noticeId, Long userId) {
        if (isRead(noticeId, userId)) {
            return;
        }
        BusNoticeRead read = new BusNoticeRead();
        read.setNoticeId(noticeId);
        read.setUserId(userId);
        read.setReadTime(new Date());
        busNoticeReadMapper.insert(read);
    }

    public boolean isRead(Long noticeId, Long userId) {
        return busNoticeReadMapper.selectCount(new QueryWrapper<BusNoticeRead>()
            .eq("notice_id", noticeId)
            .eq("user_id", userId)
        ) > 0;
    }

    public Long getReadCount(Long noticeId) {
        return busNoticeReadMapper.countByNoticeId(noticeId);
    }
}
