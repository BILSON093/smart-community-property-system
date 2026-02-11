package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.AiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AiConfigMapper extends BaseMapper<AiConfig> {
   
            @Select("SELECT * FROM ai_config WHERE enabled = 1 LIMIT 1")
    AiConfig selectEnabled();
}

