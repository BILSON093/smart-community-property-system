package com.wye.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wye.entity.BusFeeSettings;
import com.wye.mapper.FeeSettingsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FeeSettingsService {

    @Autowired
    private FeeSettingsMapper feeSettingsMapper;

    // 获取设置（如果不存在则创建默认设置）
    public BusFeeSettings getSettings() {
        QueryWrapper<BusFeeSettings> wrapper = new QueryWrapper<>();
        BusFeeSettings settings = feeSettingsMapper.selectOne(wrapper);
        if (settings == null) {
            // 创建默认设置
            settings = new BusFeeSettings();
            settings.setPropertyFee(new BigDecimal("2.5"));
            settings.setWaterFee(new BigDecimal("3.8"));
            settings.setElectricityFee(new BigDecimal("0.6"));
            settings.setGasFee(new BigDecimal("2.2"));
            feeSettingsMapper.insert(settings);
        }
        return settings;
    }

    // 保存设置
    public void saveSettings(BusFeeSettings settings) {
        QueryWrapper<BusFeeSettings> wrapper = new QueryWrapper<>();
        BusFeeSettings existingSettings = feeSettingsMapper.selectOne(wrapper);
        if (existingSettings != null) {
            // 更新现有设置
            existingSettings.setPropertyFee(settings.getPropertyFee());
            existingSettings.setWaterFee(settings.getWaterFee());
            existingSettings.setElectricityFee(settings.getElectricityFee());
            existingSettings.setGasFee(settings.getGasFee());
            feeSettingsMapper.updateById(existingSettings);
        } else {
            // 创建新设置
            feeSettingsMapper.insert(settings);
        }
    }
}
