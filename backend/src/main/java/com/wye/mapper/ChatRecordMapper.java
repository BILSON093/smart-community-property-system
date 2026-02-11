package com.wye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wye.entity.ChatRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChatRecordMapper extends BaseMapper<ChatRecord> {

    @Select("SELECT * FROM chat_record WHERE session_id = #{sessionId} ORDER BY create_time ASC")
    List<ChatRecord> selectBySessionId(Long sessionId);

    @Select("SELECT DISTINCT session_id FROM chat_record ORDER BY session_id DESC")
    List<Long> selectAllSessionIds();
}
