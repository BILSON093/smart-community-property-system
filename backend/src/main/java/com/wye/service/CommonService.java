package com.wye.service;

import cn.hutool.core.io.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CommonService {
    
    @Value("${file.upload.path}")
    private String uploadPath;
    
    @Value("${file.upload.url-prefix}")
    private String urlPrefix;
    
    /**
     * 文件上传
     */
    public Map<String, Object> upload(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 创建目录（使用绝对路径）
            File dir = new File(uploadPath).getAbsoluteFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;

            // 保存文件
            File destFile = new File(dir, fileName);
            file.transferTo(destFile);
            
            // 返回URL
            String fileUrl = urlPrefix + "/" + fileName;
            
            result.put("url", fileUrl);
            result.put("fileName", fileName);
            result.put("success", true);
            
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return result;
    }
}
