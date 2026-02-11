package com.wye.controller;

import com.wye.common.Result;
import com.wye.mapper.SysUserMapper;
import com.wye.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/temp")
public class TempController {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 临时API：更新admin密码
     */
    @PostMapping("/update-password")
    public Result<String> updatePassword() {
        try {
            String newPassword = "$2a$10$8E4R.XGTJaTJO.Tqi2QzoepaB99JnWnvAAV6LEm3a8Aw9rBPYHw/.";
            SysUser user = sysUserMapper.selectById(1L);
            if (user != null) {
                user.setPassword(newPassword);
                sysUserMapper.updateById(user);
                return Result.success("密码更新成功！");
            } else {
                return Result.error("用户不存在！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 临时API：检查admin密码
     */
    @GetMapping("/check-password")
    public Result<Map<String, String>> checkPassword() {
        SysUser user = sysUserMapper.selectById(1L);
        Map<String, String> data = new HashMap<>();
        data.put("username", user.getUsername());
        data.put("password", user.getPassword());
        return Result.success(data);
    }
}
