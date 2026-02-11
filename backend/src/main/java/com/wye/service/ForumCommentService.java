package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusForumComment;
import com.wye.entity.SysUser;
import com.wye.mapper.BusForumCommentMapper;
import com.wye.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ForumCommentService {
    
    @Autowired
    private BusForumCommentMapper busForumCommentMapper;
    
    @Autowired
    private SysUserMapper sysUserMapper;
    
    public Page<BusForumComment> list(Long forumId, int page, int size) {
        Page<BusForumComment> pageResult = busForumCommentMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusForumComment>()
                .eq("forum_id", forumId)
                .orderByDesc("create_time")
        );
        
        pageResult.getRecords().forEach(comment -> {
            SysUser user = sysUserMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUserName(user.getUsername());
                comment.setUserAvatar(user.getAvatar());
            }
        });
        
        return pageResult;
    }
    
    public void add(BusForumComment comment) {
        busForumCommentMapper.insert(comment);
    }
    
    public void delete(Long id) {
        busForumCommentMapper.deleteById(id);
    }
    
    public int countByForumId(Long forumId) {
        return Math.toIntExact(busForumCommentMapper.selectCount(
            new QueryWrapper<BusForumComment>().eq("forum_id", forumId)
        ));
    }
}
