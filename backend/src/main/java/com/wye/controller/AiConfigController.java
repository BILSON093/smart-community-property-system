package com.wye.controller;

import com.wye.common.RequireRole;
import com.wye.common.Result;
import com.wye.entity.AiConfig;
import com.wye.service.AiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-config")
@RequireRole({0})
public class AiConfigController {

    @Autowired
    private AiConfigService aiConfigService;

    /**
     * 获取所有AI配置
     */
    @GetMapping("/list")
    public Result<List<AiConfig>> getAllConfigs() {
        try {
            List<AiConfig> configs = aiConfigService.getAllConfigs();
            return Result.success(configs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * 获取启用的AI配置
     */
    @GetMapping("/enabled")
    public Result<AiConfig> getEnabledConfig() {
        try {
            AiConfig config = aiConfigService.getEnabledConfig();
            return Result.success(config);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取配置
     */
    @GetMapping("/{id}")
    public Result<AiConfig> getConfigById(@PathVariable Long id) {
        try {
            AiConfig config = aiConfigService.getConfigById(id);
            return Result.success(config);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取配置失败：" + e.getMessage());
        }
    }

    /**
     * 添加AI配置
     */
    @PostMapping("/add")
    public Result<String> addConfig(@RequestBody AiConfig config) {
        try {
            aiConfigService.addConfig(config);
            return Result.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败：" + e.getMessage());
        }
    }

    /**
     * 更新AI配置
     */
    @PostMapping("/update")
    public Result<String> updateConfig(@RequestBody AiConfig config) {
        try {
            aiConfigService.updateConfig(config);
            return Result.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除AI配置
     */
    @PostMapping("/delete/{id}")
    public Result<String> deleteConfig(@PathVariable Long id) {
        try {
            aiConfigService.deleteConfig(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 启用指定配置
     */
    @PostMapping("/enable/{id}")
    public Result<String> enableConfig(@PathVariable Long id) {
        try {
            aiConfigService.setActiveConfig(id);
            return Result.success("启用成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("启用失败：" + e.getMessage());
        }
    }

    /**
     * 禁用指定配置
     */
    @PostMapping("/disable/{id}")
    public Result<String> disableConfig(@PathVariable Long id) {
        try {
            aiConfigService.disableConfig(id);
            return Result.success("禁用成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("禁用失败：" + e.getMessage());
        }
    }
}
