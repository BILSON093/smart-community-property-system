<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>AI智能助手</span>
            <el-tag type="success" size="small">Agent模式 - 可执行操作</el-tag>
          </div>
        </div>
      </template>

    <el-card class="chat-card">
      <div class="chat-container">
        <div class="messages" ref="messagesContainer">
          <div v-if="chatMessages.length === 0" class="welcome-msg">
            <el-icon :size="48" color="#409EFF"><Promotion /></el-icon>
            <h3>你好，我是AI智能助手</h3>
            <p>我不仅能回答问题，还能帮你执行操作！</p>
            <p>试试说：帮我看看系统概况 / 催缴欠费 / 派单 / 发通知</p>
          </div>
          <div
            v-for="(msg, index) in chatMessages"
            :key="index"
            :class="['message', msg.role === 'user' ? 'user' : 'assistant']"
          >
            <div class="message-avatar">
              <el-avatar :size="36" :style="{ background: msg.role === 'user' ? '#409EFF' : '#67C23A' }">
                {{ msg.role === 'user' ? '我' : 'AI' }}
              </el-avatar>
            </div>
            <div class="message-body">
              <div class="message-content" v-html="formatMessage(msg.content)"></div>
              <div v-if="msg.toolCalls && msg.toolCalls.length > 0" class="tool-calls">
                <div v-for="(tc, ti) in msg.toolCalls" :key="ti" class="tool-call-item">
                  <el-tag size="small" type="warning">
                    <el-icon><Cpu /></el-icon> {{ tc.tool }}
                  </el-tag>
                  <span class="tool-result">{{ formatToolResult(tc.result) }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="loading" class="message assistant">
            <div class="message-avatar">
              <el-avatar :size="36" :style="{ background: '#67C23A' }">AI</el-avatar>
            </div>
            <div class="message-body">
              <div class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <el-input
            v-model="inputMessage"
            placeholder="输入消息，如：帮我看看系统概况、催缴欠费、派单给张师傅、发通知..."
            @keyup.enter="sendMessage"
            :disabled="loading"
            clearable
          />
          <el-button type="primary" @click="sendMessage" :loading="loading">
            发送
          </el-button>
          <el-button @click="clearChat">
            <el-icon><Delete /></el-icon> 清空
          </el-button>
        </div>
      </div>
    </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const inputMessage = ref('')
const chatMessages = ref([])
const messagesContainer = ref(null)
const loading = ref(false)

const userInfo = computed(() => JSON.parse(localStorage.getItem('userInfo') || '{}'))

const sendMessage = async () => {
  const msg = inputMessage.value.trim()
  if (!msg || loading.value) return

  chatMessages.value.push({ role: 'user', content: msg })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const history = chatMessages.value.slice(-10).map(m => ({
      role: m.role,
      content: m.content
    }))

    const res = await request.post('/agent/chat', {
      message: msg,
      history: history.slice(0, -1)
    })

    if (res.data) {
      chatMessages.value.push({
        role: 'assistant',
        content: res.data.reply || '抱歉，我无法处理您的请求。',
        toolCalls: res.data.tool_calls || []
      })
    }
  } catch (error) {
    chatMessages.value.push({
      role: 'assistant',
      content: '抱歉，服务暂时不可用，请稍后再试。'
    })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const formatMessage = (content) => {
  if (!content) return ''
  return content
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/\n/g, '<br>')
}

const formatToolResult = (result) => {
  if (!result) return ''
  try {
    const obj = JSON.parse(result)
    if (obj.error) return '❌ ' + obj.error
    if (obj.result) return '✅ ' + obj.result
    return result
  } catch {
    return result.length > 80 ? result.substring(0, 80) + '...' : result
  }
}

const clearChat = () => {
  chatMessages.value = []
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
.chat-card {
  height: 600px;
  border-radius: var(--radius-md);
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 520px;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background-color: #F8FAFC;
  border-radius: var(--radius-sm);
}

.welcome-msg {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-muted);
}

.welcome-msg h3 {
  margin: 16px 0 8px;
  color: var(--text-primary);
  font-weight: 600;
}

.welcome-msg p {
  margin: 4px 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.message {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-body {
  max-width: 70%;
}

.message.user .message-body {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-content {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-content {
  background: var(--primary-gradient);
  color: white;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.25);
  border-top-right-radius: 4px;
}

.message.assistant .message-content {
  background-color: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border-top-left-radius: 4px;
}

.tool-calls {
  margin-top: 8px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tool-call-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

.tool-result {
  color: var(--success);
  font-size: 12px;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: white;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  border-radius: 16px;
  border-top-left-radius: 4px;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #CBD5E1;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

.input-area {
  display: flex;
  gap: 10px;
  margin-top: 12px;
}

.input-area .el-input {
  flex: 1;
}
</style>
