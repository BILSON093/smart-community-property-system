<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>在线客服</span>
          </div>
        </div>
      </template>

    <el-row :gutter="20">
      <el-col :span="6">
        <el-card style="height: 600px">
          <template #header>
            <span>用户列表</span>
          </template>
          <el-scrollbar>
            <div
              v-for="user in userList"
              :key="user.id"
              class="user-item"
              :class="{ active: selectedUserId === user.id }"
              @click="selectUser(user)"
            >
              <el-avatar :src="user.avatar" />
              <div class="user-info">
                <div class="user-name">{{ user.name }}</div>
                <div class="user-address">{{ user.building }} {{ user.unit }} {{ user.room }}</div>
              </div>
            </div>
          </el-scrollbar>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card style="height: 600px">
          <template #header>
          <div class="chat-header">
            <span>{{ selectedUser?.name || '选择用户开始聊天' }}</span>
            <span v-if="selectedUser" class="user-detail-address">{{ selectedUser.building }} {{ selectedUser.unit }} {{ selectedUser.room }}</span>
          </div>
        </template>
          <div class="chat-container">
            <div class="messages" ref="messagesContainer">
              <div
                v-for="msg in messages"
                :key="msg.id"
                :class="['message', msg.sender === 2 ? 'admin' : 'user']"
              >
                <el-avatar :src="msg.avatar" :size="40" class="message-avatar" />
                <div class="message-content-wrapper">
                  <div class="sender-name">{{ msg.senderName }}</div>
                  <div class="content">
                    <template v-for="(segment, idx) in parseContent(msg.content)" :key="idx">
                      <a v-if="segment.type === 'link'" :href="segment.url" :target="segment.url.startsWith('/') ? '_self' : '_blank'" class="chat-link">{{ segment.text }}</a>
                      <span v-else>{{ segment.text }}</span>
                    </template>
                  </div>
                  <div class="time">{{ formatTime(msg.createTime) }}</div>
                </div>
              </div>
            </div>
            <div class="input-area">
              <el-input
                v-model="messageInput"
                placeholder="输入消息"
                @keyup.enter="sendMessage"
              />
              <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { connectChat } from '@/utils/realtime'

const userList = ref([])
const selectedUserId = ref(null)
const selectedUser = ref(null)
const messages = ref([])
const messageInput = ref('')
const messagesContainer = ref(null)
const sending = ref(false)
const adminInfo = ref({ avatar: '', name: '管理员' })

const parseContent = (content) => {
  if (!content) return []
  const segments = []
  const regex = /\[([^\]]+)\]\(([^)]+)\)/g
  let lastIndex = 0
  let match
  while ((match = regex.exec(content)) !== null) {
    if (match.index > lastIndex) {
      segments.push({ type: 'text', text: content.slice(lastIndex, match.index) })
    }
    segments.push({ type: 'link', text: match[1], url: match[2] })
    lastIndex = regex.lastIndex
  }
  if (lastIndex < content.length) {
    segments.push({ type: 'text', text: content.slice(lastIndex) })
  }
  return segments
}

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  
  try {
    const date = new Date(timeStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
  } catch (e) {
    return timeStr
  }
}

let pollingTimer = null
let chatSocket = null

onMounted(() => {
  loadAdminInfo()
  loadUsers()
  chatSocket = connectChat((message) => {
    if (message.event !== 'chat.message') return
    const payload = message.payload || {}
    loadUsers()
    if (selectedUserId.value && payload.sessionId === selectedUserId.value) {
      if (!messages.value.some(item => item.id === payload.id)) {
        messages.value.push(payload)
        scrollToBottom()
      }
    }
  })
  pollingTimer = setInterval(() => {
    if (selectedUserId.value) {
      loadMessages()
    }
  }, 3000)
})

onBeforeUnmount(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
  if (chatSocket) {
    chatSocket.close()
    chatSocket = null
  }
})

const loadAdminInfo = async () => {
  try {
    const res = await request.get('/admin/info')
    if (res.data) {
      adminInfo.value = {
        avatar: res.data.avatar || '',
        name: res.data.realName || '管理员'
      }
    }
  } catch (error) {
    console.error('加载管理员信息失败', error)
  }
}

const selectUser = async (user) => {
  selectedUserId.value = user.id
  selectedUser.value = user
  await loadMessages()
  scrollToBottom()
}

const loadUsers = async () => {
  try {
    const res = await request.get('/chat/admin/sessions')
    if (res.data && res.data.length > 0) {
      userList.value = res.data
    } else {
      userList.value = []
    }
  } catch (error) {
    console.error('加载用户列表失败', error)
    userList.value = []
  }
}

const loadMessages = async () => {
  if (!selectedUserId.value) return

  try {
    const res = await request.get('/chat/admin/session', {
      params: { sessionId: selectedUserId.value }
    })
    if (res.data) {
      messages.value = res.data
    } else {
      messages.value = []
    }
  } catch (error) {
    console.error('加载消息失败', error)
    messages.value = []
  }
}

const sendMessage = async () => {
  if (!selectedUserId.value) {
    ElMessage.warning('请先选择用户')
    return
  }

  if (!messageInput.value.trim()) {
    ElMessage.warning('请输入消息内容')
    return
  }

  sending.value = true

  try {
    await request.post('/chat/admin/reply', {
      sessionId: selectedUserId.value,
      sender: 2,
      content: messageInput.value,
      msgType: 'text'
    })

    messageInput.value = ''
    ElMessage.success('发送成功')
    await loadMessages()
    scrollToBottom()
  } catch (error) {
    ElMessage.error('发送失败：' + (error.message || '未知错误'))
  } finally {
    sending.value = false
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
.user-item {
  display: flex;
  align-items: center;
  padding: 12px;
  cursor: pointer;
  border-radius: var(--radius-sm);
  margin-bottom: 6px;
  transition: all 0.2s ease;
}

.user-item:hover {
  background-color: var(--primary-bg);
}

.user-item.active {
  background-color: rgba(79, 110, 247, 0.1);
  box-shadow: inset 3px 0 0 var(--primary);
}

.user-info {
  margin-left: 10px;
  flex: 1;
  overflow: hidden;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.user-address {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.user-detail-address {
  font-size: 12px;
  color: var(--text-muted);
  margin-left: 12px;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #F8FAFC;
  border-radius: var(--radius-sm);
}

.message {
  margin-bottom: 16px;
  display: flex;
  gap: 10px;
}

.message.user {
  flex-direction: row;
}

.message.admin {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.message.user .message-content-wrapper {
  align-items: flex-start;
}

.message.admin .message-content-wrapper {
  align-items: flex-end;
}

.sender-name {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.message.user .sender-name {
  text-align: left;
}

.message.admin .sender-name {
  text-align: right;
}

.content {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  display: inline-block;
  width: fit-content;
  max-width: 100%;
}

.message.user .content {
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border-top-left-radius: 4px;
}

.message.admin .content {
  background: var(--primary-gradient);
  color: white;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.25);
  border-top-right-radius: 4px;
}

.message-content a.chat-link {
  color: var(--primary);
  text-decoration: none;
  font-weight: 500;
  border-bottom: 1px solid var(--primary);
  padding-bottom: 2px;
  transition: all 0.3s;
}

.message-content a.chat-link:hover {
  color: var(--primary-light);
  border-bottom-color: var(--primary-light);
  text-decoration: none;
}

.message.admin .message-content a.chat-link {
  color: rgba(255, 255, 255, 0.9);
  border-bottom-color: rgba(255, 255, 255, 0.9);
}

.message.admin .message-content a.chat-link:hover {
  color: white;
  border-bottom-color: white;
}

.time {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.input-area {
  display: flex;
  gap: 10px;
  padding: 16px;
  background-color: white;
  border-top: 1px solid #F1F5F9;
  border-radius: 0 0 var(--radius-md) var(--radius-md);
}
</style>
