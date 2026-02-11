package com.wye.controller;

import com.wye.common.Result;
import com.wye.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/common")
public class CommonController {
    
    @Autowired
    private CommonService commonService;
    
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public Result<Map<String, Object>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        Map<String, Object> result = commonService.upload(file);
        if ((Boolean) result.get("success")) {
            // 返回文件的URL路径
            Map<String, Object> data = new HashMap<>();
            data.put("url", result.get("url"));
            data.put("fileName", result.get("fileName"));
            return Result.success(data);
        } else {
            return Result.error((String) result.get("message"));
        }
    }
}
