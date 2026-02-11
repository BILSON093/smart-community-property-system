package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.entity.BusForumLike;
import com.wye.mapper.BusForumLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ForumLikeService {
    
    @Autowired
    private BusForumLikeMapper busForumLikeMapper;
    
    @Transactional
    public void toggle(Long forumId, Long userId) {
        QueryWrapper<BusForumLike> wrapper = new QueryWrapper<BusForumLike>()
            .eq("forum_id", forumId)
            .eq("user_id", userId);
        
        BusForumLike existing = busForumLikeMapper.selectOne(wrapper);
        
        if (existing != null) {
            busForumLikeMapper.deleteById(existing.getId());
        } else {
            BusForumLike like = new BusForumLike();
            like.setForumId(forumId);
            like.setUserId(userId);
            busForumLikeMapper.insert(like);
        }
    }
    
    public boolean isLiked(Long forumId, Long userId) {
        Long count = busForumLikeMapper.selectCount(new QueryWrapper<BusForumLike>()
            .eq("forum_id", forumId)
            .eq("user_id", userId));
        return count != null && count > 0;
    }
    
    public int countByForumId(Long forumId) {
        Long count = busForumLikeMapper.selectCount(
            new QueryWrapper<BusForumLike>().eq("forum_id", forumId)
        );
        return count != null ? count.intValue() : 0;
    }
}
