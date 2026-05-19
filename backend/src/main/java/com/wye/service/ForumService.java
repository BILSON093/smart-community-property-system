package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusForum;
import com.wye.entity.BusForumCategory;
import com.wye.entity.SysUser;
import com.wye.mapper.BusForumMapper;
import com.wye.mapper.SysUserMapper;
import com.wye.mapper.BusForumCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumService {
    
    @Autowired
    private BusForumMapper busForumMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    @Autowired
    private BusForumCategoryMapper busForumCategoryMapper;
    
    @Autowired
    private ForumCommentService forumCommentService;
    
    @Autowired
    private ForumLikeService forumLikeService;
    
    public Page<BusForum> list(int page, int size) {
        return list(page, size, null);
    }
    
    public Page<BusForum> list(int page, int size, Long categoryId) {
        return list(page, size, categoryId, null);
    }
    
    public Page<BusForum> list(int page, int size, Long categoryId, String keyword) {
        try {
            QueryWrapper<BusForum> wrapper = new QueryWrapper<BusForum>()
                .eq("status", 1)
                .orderByDesc("is_pinned")
                .orderByDesc("id");

            if (categoryId != null) {
                wrapper.eq("category_id", categoryId);
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                wrapper.and(w -> w.like("title", keyword).or().like("content", keyword));
            }

            Page<BusForum> pageResult = busForumMapper.selectPage(new Page<>(page, size), wrapper);
            pageResult.getRecords().forEach(forum -> enrichForumInfo(forum, null));
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return new Page<>(page, size);
        }
    }

    public Page<BusForum> listAll(int page, int size, Integer status) {
        try {
            QueryWrapper<BusForum> wrapper = new QueryWrapper<BusForum>()
                .orderByDesc("is_pinned")
                .orderByDesc("id");
            if (status != null) {
                wrapper.eq("status", status);
            }
            Page<BusForum> pageResult = busForumMapper.selectPage(new Page<>(page, size), wrapper);
            pageResult.getRecords().forEach(forum -> enrichForumInfo(forum, null));
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return new Page<>(page, size);
        }
    }
    
    public Page<BusForum> listForUser(int page, int size, Long userId) {
        try {
            QueryWrapper<BusForum> wrapper = new QueryWrapper<BusForum>()
                .eq("user_id", userId)
                .orderByDesc("create_time");
            
            Page<BusForum> pageResult = busForumMapper.selectPage(new Page<>(page, size), wrapper);
            
            pageResult.getRecords().forEach(forum -> {
                enrichForumInfo(forum, userId);
            });
            
            return pageResult;
        } catch (Exception e) {
            e.printStackTrace();
            return new Page<>(page, size);
        }
    }
    
    private void enrichForumInfo(BusForum forum, Long currentUserId) {
        if (forum.getUserId() != null) {
            SysUser user = sysUserMapper.selectById(forum.getUserId());
            if (user != null) {
                forum.setUserName(user.getUsername());
                forum.setUserAvatar(user.getAvatar());
            }
        }
        
        if (forum.getCategoryId() != null) {
            BusForumCategory category = busForumCategoryMapper.selectById(forum.getCategoryId());
            if (category != null) {
                forum.setCategoryName(category.getName());
            }
        }
        
        forum.setCommentCount(forumCommentService.countByForumId(forum.getId()));
        forum.setLikeCount(forumLikeService.countByForumId(forum.getId()));
        
        if (currentUserId != null) {
            forum.setIsLiked(forumLikeService.isLiked(forum.getId(), currentUserId));
        }
    }
    
    public BusForum getById(Long id, Long currentUserId) {
        BusForum forum = busForumMapper.selectById(id);
        if (forum != null) {
            enrichForumInfo(forum, currentUserId);
        }
        return forum;
    }
    
    public void add(BusForum forum) {
        if (forum.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }
        busForumMapper.insert(forum);
    }
    
    public void delete(Long id) {
        busForumMapper.deleteById(id);
    }

    public void update(BusForum forum) {
        busForumMapper.updateById(forum);
    }
    
    public List<BusForum> getByUserId(Long userId) {
        return busForumMapper.selectList(
            new QueryWrapper<BusForum>()
                .eq("user_id", userId)
                .orderByDesc("create_time")
        );
    }

    public void approve(Long id) {
        BusForum forum = busForumMapper.selectById(id);
        if (forum != null) {
            forum.setStatus(1);
            busForumMapper.updateById(forum);
        }
    }

    public void reject(Long id) {
        BusForum forum = busForumMapper.selectById(id);
        if (forum != null) {
            forum.setStatus(2);
            busForumMapper.updateById(forum);
        }
    }
}
