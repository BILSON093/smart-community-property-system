package com.wye.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wye.common.Result;
import com.wye.entity.BusForum;
import com.wye.entity.BusForumCategory;
import com.wye.entity.BusForumComment;
import com.wye.service.ForumService;
import com.wye.service.ForumCategoryService;
import com.wye.service.ForumCommentService;
import com.wye.service.ForumLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumController {
    
    @Autowired
    private ForumService forumService;
    
    @Autowired
    private ForumCategoryService forumCategoryService;
    
    @Autowired
    private ForumCommentService forumCommentService;
    
    @Autowired
    private ForumLikeService forumLikeService;
    
    @GetMapping("/list")
    public Result<Page<BusForum>> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) String keyword) {
        return Result.success(forumService.list(page, size, categoryId, keyword));
    }
    
    @GetMapping("/list/user")
    public Result<Page<BusForum>> listForUser(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(forumService.listForUser(page, size, userId));
    }
    
    @GetMapping("/{id}")
    public Result<BusForum> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(forumService.getById(id, userId));
    }
    
    @PostMapping("/add")
    public Result<String> add(@RequestBody BusForum forum, HttpServletRequest request) {
        System.out.println("=== [ForumController.add] 接收到 images: " + forum.getImages());
        Long userId = (Long) request.getAttribute("userId");
        forum.setUserId(userId);
        forumService.add(forum);
        return Result.success("发布成功");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        forumService.delete(id);
        return Result.success("删除成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody BusForum forum) {
        forumService.update(forum);
        return Result.success("更新成功");
    }
    
    @PutMapping("/{id}/pin")
    public Result<String> pin(@PathVariable Long id) {
        BusForum forum = new BusForum();
        forum.setId(id);
        forum.setIsPinned(1);
        forumService.update(forum);
        return Result.success("置顶成功");
    }
    
    @PutMapping("/{id}/unpin")
    public Result<String> unpin(@PathVariable Long id) {
        BusForum forum = new BusForum();
        forum.setId(id);
        forum.setIsPinned(0);
        forumService.update(forum);
        return Result.success("取消置顶成功");
    }
    
    @GetMapping("/my-posts")
    public Result<List<BusForum>> getMyPosts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(forumService.getByUserId(userId));
    }
    
    @GetMapping("/category/list")
    public Result<List<BusForumCategory>> categoryList() {
        return Result.success(forumCategoryService.listAll());
    }
    
    @GetMapping("/category/list/page")
    public Result<Page<BusForumCategory>> categoryListPage(@RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumCategoryService.list(page, size));
    }
    
    @GetMapping("/category/{id}")
    public Result<BusForumCategory> categoryGetById(@PathVariable Long id) {
        return Result.success(forumCategoryService.getById(id));
    }
    
    @PostMapping("/category/add")
    public Result<String> categoryAdd(@RequestBody BusForumCategory category) {
        forumCategoryService.add(category);
        return Result.success("添加成功");
    }
    
    @PutMapping("/category/update")
    public Result<String> categoryUpdate(@RequestBody BusForumCategory category) {
        forumCategoryService.update(category);
        return Result.success("更新成功");
    }
    
    @DeleteMapping("/category/{id}")
    public Result<String> categoryDelete(@PathVariable Long id) {
        forumCategoryService.delete(id);
        return Result.success("删除成功");
    }
    
    @GetMapping("/{forumId}/comments")
    public Result<Page<BusForumComment>> getComments(@PathVariable Long forumId,
                                                     @RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return Result.success(forumCommentService.list(forumId, page, size));
    }
    
    @PostMapping("/{forumId}/comments")
    public Result<String> addComment(@PathVariable Long forumId, @RequestBody BusForumComment comment, HttpServletRequest request) {
        System.out.println("=== [ForumController.addComment] 接收到评论: " + comment);
        System.out.println("=== [ForumController.addComment] 接收到 images: " + comment.getImages());
        try {
            Long userId = (Long) request.getAttribute("userId");
            System.out.println("=== [ForumController.addComment] userId: " + userId);
            comment.setForumId(forumId);
            comment.setUserId(userId);
            forumCommentService.add(comment);
            return Result.success("评论成功");
        } catch (Exception e) {
            System.out.println("=== [ForumController.addComment] 错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("评论失败");
        }
    }
    
    @DeleteMapping("/comments/{id}")
    public Result<String> deleteComment(@PathVariable Long id) {
        forumCommentService.delete(id);
        return Result.success("删除成功");
    }
    
    @PostMapping("/{forumId}/like")
    public Result<String> toggleLike(@PathVariable Long forumId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        forumLikeService.toggle(forumId, userId);
        return Result.success("操作成功");
    }
    
    @GetMapping("/{forumId}/like/check")
    public Result<Boolean> checkLike(@PathVariable Long forumId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(forumLikeService.isLiked(forumId, userId));
    }
}
