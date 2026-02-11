package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.entity.BusForumCategory;
import com.wye.mapper.BusForumCategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ForumCategoryService {
    
    @Autowired
    private BusForumCategoryMapper busForumCategoryMapper;
    
    public Page<BusForumCategory> list(int page, int size) {
        return busForumCategoryMapper.selectPage(new Page<>(page, size),
            new QueryWrapper<BusForumCategory>().orderByAsc("sort_order")
        );
    }
    
    public List<BusForumCategory> listAll() {
        return busForumCategoryMapper.selectList(
            new QueryWrapper<BusForumCategory>().orderByAsc("sort_order")
        );
    }
    
    public BusForumCategory getById(Long id) {
        return busForumCategoryMapper.selectById(id);
    }
    
    public void add(BusForumCategory category) {
        busForumCategoryMapper.insert(category);
    }
    
    public void update(BusForumCategory category) {
        busForumCategoryMapper.updateById(category);
    }
    
    public void delete(Long id) {
        busForumCategoryMapper.deleteById(id);
    }
}
