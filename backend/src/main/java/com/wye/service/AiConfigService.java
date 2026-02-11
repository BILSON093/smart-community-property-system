package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.entity.AiConfig;
import com.wye.mapper.AiConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AiConfigService {

    @Autowired
    private AiConfigMapper aiConfigMapper;

    /**
      * 获取所有AI配置
      */
            public List<AiConfig> getAllConfigs() {
        return aiConfigMapper.selectList(null);
    }

    /**
      * 获取启用的AI配置
      */
            public AiConfig getEnabledConfig() {
        return aiConfigMapper.selectEnabled();
    }

    /**
      * 根据ID获取配置
      */
            public AiConfig getConfigById(Long id) {
        return aiConfigMapper.selectById(id);
    }

    /**
      * 添加AI配置
      */
            public void addConfig(AiConfig config) {
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());
        aiConfigMapper.insert(config);
    }

    /**
      * 更新AI配置
      */
            public void updateConfig(AiConfig config) {
        config.setUpdateTime(new Date());
        aiConfigMapper.updateById(config);
    }

    /**
      * 删除AI配置
      */
            public void deleteConfig(Long id) {
        aiConfigMapper.deleteById(id);
    }

    /**
      * 启用配置
      */
            public void enableConfig(Long id) {
        AiConfig config = aiConfigMapper.selectById(id);
        if (config != null) {
            config.setEnabled(1);
            config.setUpdateTime(new Date());
            aiConfigMapper.updateById(config);
        }
    }

    /**
      * 禁用配置
      */
            public void disableConfig(Long id) {
        AiConfig config = aiConfigMapper.selectById(id);
        if (config != null) {
            config.setEnabled(0);
            config.setUpdateTime(new Date());
            aiConfigMapper.updateById(config);
        }
    }

    /**
      * 启用指定配置并禁用其他配置
      */
            public void setActiveConfig(Long id) {
        List<AiConfig> allConfigs = aiConfigMapper.selectList(null);
        for (AiConfig config : allConfigs) {
            if (config.getId().equals(id)) {
                config.setEnabled(1);
            } else {
                config.setEnabled(0);
            }
            config.setUpdateTime(new Date());
            aiConfigMapper.updateById(config);
        }
    }
}

