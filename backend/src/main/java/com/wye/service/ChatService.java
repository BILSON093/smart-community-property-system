package com.wye.service;

import com.wye.entity.ChatRecord;
import com.wye.entity.SysOwner;
import com.wye.entity.SysUser;
import com.wye.mapper.ChatRecordMapper;
import com.wye.mapper.SysOwnerMapper;
import com.wye.mapper.SysUserMapper;
import com.wye.websocket.WebSocketPushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private ChatRecordMapper chatRecordMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysOwnerMapper sysOwnerMapper;

    @Autowired
    private WebSocketPushService webSocketPushService;

    /**
     * 发送消息
     */
    public void sendMessage(Long userId, Integer sender, String content, String msgType) {
        ChatRecord record = new ChatRecord();
        record.setSessionId(userId); // sessionId 实际上是 userId
        record.setSender(sender);
        record.setContent(content);
        record.setMsgType(msgType);
        record.setCreateTime(new java.util.Date());
        chatRecordMapper.insert(record);

        Map<String, Object> payload = buildMessagePayload(record, userId);
        payload.put("sessionId", userId);
        webSocketPushService.pushChatMessage(userId, payload);
    }

    /**
     * 获取会话记录
     */
    public List<Map<String, Object>> getSession(Long userId) {
        List<ChatRecord> records = chatRecordMapper.selectBySessionId(userId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatRecord record : records) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("id", record.getId());
            msgMap.put("sender", record.getSender());
            msgMap.put("content", record.getContent());
            msgMap.put("msgType", record.getMsgType());
            msgMap.put("createTime", record.getCreateTime());

            String avatar = "";
            String senderName = "";

            if (record.getSender() == 1) {
                SysUser user = sysUserMapper.selectById(userId);
                if (user != null) {
                    avatar = user.getAvatar();
                    senderName = user.getRealName();
                }
            } else if (record.getSender() == 2) {
                SysUser admin = sysUserMapper.selectByRole(0);
                if (admin != null) {
                    avatar = admin.getAvatar();
                    senderName = admin.getRealName();
                }
            }

            msgMap.put("avatar", avatar);
            msgMap.put("senderName", senderName);
            result.add(msgMap);
        }

        return result;
    }

    private Map<String, Object> buildMessagePayload(ChatRecord record, Long sessionId) {
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put("id", record.getId());
        msgMap.put("sessionId", sessionId);
        msgMap.put("sender", record.getSender());
        msgMap.put("content", record.getContent());
        msgMap.put("msgType", record.getMsgType());
        msgMap.put("createTime", record.getCreateTime());

        String avatar = "";
        String senderName = "";
        if (record.getSender() != null && record.getSender() == 1) {
            SysUser user = sysUserMapper.selectById(sessionId);
            if (user != null) {
                avatar = user.getAvatar();
                senderName = user.getRealName();
            }
        } else if (record.getSender() != null && record.getSender() == 2) {
            SysUser admin = sysUserMapper.selectByRole(0);
            if (admin != null) {
                avatar = admin.getAvatar();
                senderName = admin.getRealName();
            }
        }

        msgMap.put("avatar", avatar);
        msgMap.put("senderName", senderName);
        return msgMap;
    }

    /**
     * 获取所有会话列表
     */
    public List<Long> getAllSessions() {
        return chatRecordMapper.selectAllSessionIds();
    }

    /**
     * 获取所有会话列表（带业主信息）
     */
    public List<Map<String, Object>> getAllSessionsWithInfo() {
        List<Long> userIds = chatRecordMapper.selectAllSessionIds();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long userId : userIds) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userId);

            SysUser user = sysUserMapper.selectById(userId);
            if (user != null) {
                userInfo.put("name", user.getRealName());
                userInfo.put("avatar", user.getAvatar());

                SysOwner owner = sysOwnerMapper.selectByUserId(userId);
                if (owner != null) {
                    StringBuilder address = new StringBuilder();
                    String building = owner.getBuilding() != null ? owner.getBuilding() : "";
                    String unit = owner.getUnit() != null ? owner.getUnit() : "";
                    String room = owner.getRoom() != null ? owner.getRoom() : "";
                    
                    address.append(building).append("栋");
                    if (!unit.isEmpty()) {
                        address.append(unit).append("单元");
                    }
                    if (!room.isEmpty()) {
                        address.append(room).append("室");
                    }
                    userInfo.put("address", address.toString());
                    userInfo.put("building", building);
                    userInfo.put("unit", unit);
                    userInfo.put("room", room);
                } else {
                    userInfo.put("address", "未设置房号");
                    userInfo.put("building", "");
                    userInfo.put("unit", "");
                    userInfo.put("room", "");
                }
            } else {
                userInfo.put("name", "未知用户");
                userInfo.put("avatar", "");
                userInfo.put("address", "");
                userInfo.put("building", "");
                userInfo.put("unit", "");
                userInfo.put("room", "");
            }

            result.add(userInfo);
        }

        return result;
    }

    /**
     * 管理员获取指定会话的消息（带头像）
     */
    public List<Map<String, Object>> getSessionWithAvatar(Long sessionId) {
        List<ChatRecord> records = chatRecordMapper.selectBySessionId(sessionId);
        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatRecord record : records) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("id", record.getId());
            msgMap.put("sender", record.getSender());
            msgMap.put("content", record.getContent());
            msgMap.put("msgType", record.getMsgType());
            msgMap.put("createTime", record.getCreateTime());

            String avatar = "";
            String senderName = "";

            if (record.getSender() == 1) {
                SysUser user = sysUserMapper.selectById(sessionId);
                if (user != null) {
                    avatar = user.getAvatar();
                    senderName = user.getRealName();
                }
            } else if (record.getSender() == 2) {
                SysUser admin = sysUserMapper.selectByRole(0);
                if (admin != null) {
                    avatar = admin.getAvatar();
                    senderName = admin.getRealName();
                }
            }

            msgMap.put("avatar", avatar);
            msgMap.put("senderName", senderName);
            result.add(msgMap);
        }

        return result;
    }

    /**
     * 获取管理员信息
     */
    public Map<String, Object> getAdminInfo() {
        SysUser admin = sysUserMapper.selectByRole(0);
        Map<String, Object> adminMap = new HashMap<>();
        if (admin != null) {
            adminMap.put("id", admin.getId());
            adminMap.put("realName", admin.getRealName());
            adminMap.put("avatar", admin.getAvatar());
        } else {
            adminMap.put("id", 0);
            adminMap.put("realName", "客服");
            adminMap.put("avatar", "");
        }
        return adminMap;
    }
}
