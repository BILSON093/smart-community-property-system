<template>
  <div class="chat-container">
    <div class="chat-header">
      <div class="header-left">
        <div class="header-title">
          <van-icon name="chat-o" size="28" color="#fff" />
          <h2>{{ modeLabel }}</h2>
        </div>
      </div>
      <div class="header-actions">
        <button @click="switchToAgent" class="btn-switch" :class="{ active: chatMode === 'agent' }">
          <van-icon name="fire-o" size="18" />
          智能助手
        </button>
        <button @click="switchToAI" class="btn-switch" :class="{ active: chatMode === 'ai' }">
          <van-icon name="chat-o" size="18" />
          AI客服
        </button>
        <button @click="switchToHuman" class="btn-switch" :class="{ active: chatMode === 'manual' }">
          <van-icon name="service" size="18" />
          人工
        </button>
      </div>
    </div>

    <div class="chat-content">
      <div class="message-list" ref="messageList">
        <div
          v-for="msg in messages"
          :key="msg.id"
          :class="['message', msg.role === 'user' ? 'right' : 'left']"
        >
          <img :src="msg.avatar" class="message-avatar" alt="avatar" />
          <div class="message-content-wrapper">
            <div class="message-bubble">
              <img v-if="msg.image" :src="msg.image" class="message-image" />
              <div class="message-content">
                <template v-for="(segment, sIdx) in parseContent(msg.content)" :key="sIdx">
                  <a v-if="segment.type === 'link'" href="#" class="chat-link" @click.prevent="handleLinkClick(segment.path || segment.url)">{{ segment.text }}</a>
                  <span v-else>{{ segment.text }}</span>
                </template>
              </div>
            </div>
            <div class="message-time">{{ formatTime(msg.createTime) }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <div class="input-wrapper">
        <div class="image-preview" v-if="selectedImage">
          <img :src="selectedImage" class="preview-img" />
          <van-icon name="cross" class="remove-img" @click="removeImage" />
        </div>
        <div class="input-actions">
          <van-uploader
            v-model="fileList"
            :max-count="1"
            :after-read="afterRead"
            accept="image/*"
            :show-upload-list="false"
            :preview-size="0"
            :v-show="false"
            class="upload-btn"
          >
            <van-icon name="photo-o" size="20" />
          </van-uploader>
          <input
            v-model="input"
            @keyup.enter="send"
            placeholder="请输入您的问题..."
            class="chat-input"
          />
        </div>
        <button @click="send" class="btn-send">
          <van-icon name="send-gift-o" size="20" />
          发送
        </button>
      </div>
      <div class="quick-questions" v-if="chatMode !== 'manual'">
        <p class="quick-title">{{ chatMode === 'agent' ? '试试对我说：' : '常见问题：' }}</p>
        <div class="quick-tags">
          <span
            v-for="(q, index) in currentQuickQuestions"
            :key="index"
            @click="quickAsk(q)"
            class="quick-tag"
          >
            {{ q }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'
import router from '@/router'
import { connectChat } from '@/utils/realtime'

const isHuman = ref(false)
const chatMode = ref('agent')
const input = ref('')
const messages = ref([])
const selectedImage = ref('')
const fileList = ref([])
const quickQuestions = ref([
  '如何缴纳物业费？',
  '报修流程是什么？',
  '社区活动有哪些？',
  '如何联系物业？',
  '停车如何办理？'
])
const agentQuickQuestions = ref([
  '我欠了多少钱？',
  '我家水管漏水了',
  '我的报修处理了吗？',
  '最近有什么通知？',
  '给工单打个5分',
  '我要提交反馈'
])
const currentQuickQuestions = computed(() => {
  return chatMode.value === 'agent' ? agentQuickQuestions.value : quickQuestions.value
})
const modeLabel = computed(() => {
  if (chatMode.value === 'agent') return 'AI智能助手'
  if (chatMode.value === 'ai') return 'AI智能客服'
  return '人工客服'
})
const messageList = ref(null)
const userInfo = ref({ avatar: '', name: '' })
const adminInfo = ref({ avatar: '', name: '' })
let chatSocket = null
let chatPollingTimer = null


const loadUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    if (res.data) {
      userInfo.value = {
        avatar: res.data.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
        name: res.data.realName || ''
      }
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
    userInfo.value = {
      avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      name: ''
    }
  }
}

const loadAdminInfo = async () => {
  try {
    const res = await request.get('/chat/admin/info')
    if (res.data) {
      adminInfo.value = {
        avatar: res.data.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        name: res.data.realName || '客服'
      }
    }
  } catch (error) {
    console.error('加载管理员信息失败', error)
    adminInfo.value = {
      avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      name: '客服'
    }
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageList.value) {
      // 使用更可靠的滚动方式
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

// 确保在所有消息更新后都滚动到最新消息
const addMessage = (message) => {
  messages.value.push(message)
  scrollToBottom()
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

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
    const text = match[1]
    let url = match[2]
    if (url.startsWith('/')) {
      let routePath = url
      if (url.includes('/pages/activity/')) routePath = '/activity'
      else if (url.includes('/pages/fee/')) routePath = '/pay'
      else if (url.includes('/pages/repair/')) routePath = '/repair'
      else if (url.includes('/pages/report/')) routePath = '/repair'
      else if (url.includes('/pages/forum/')) routePath = '/forum'
      else if (url.includes('/pages/parking/')) routePath = '/pay'
      segments.push({ type: 'link', text, path: routePath })
    } else {
      segments.push({ type: 'link', text, url })
    }
    lastIndex = regex.lastIndex
  }
  if (lastIndex < content.length) {
    segments.push({ type: 'text', text: content.slice(lastIndex) })
  }
  return segments
}

const handleLinkClick = (target) => {
  if (target && target.startsWith('/')) {
    router.push(target)
  } else if (target) {
    window.open(target, '_blank')
  }
}

const switchToAgent = () => {
  if (chatMode.value === 'agent') return
  stopManualRealtime()
  chatMode.value = 'agent'
  isHuman.value = false
  messages.value = []
  messages.value.push({
    id: 1,
    role: 'ai',
    content: '您好！我是智能助手，不仅能回答问题，还能帮您报修、缴费、查进度等操作！',
    createTime: new Date(),
    avatar: adminInfo.value.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  })
  scrollToBottom()
}

const switchToAI = () => {
  if (chatMode.value === 'ai') return
  stopManualRealtime()
  chatMode.value = 'ai'
  isHuman.value = false
  messages.value = []
  messages.value.push({
    id: 1,
    role: 'ai',
    content: '您好，我是智慧社区的智能客服，请问有什么可以帮助您？',
    createTime: new Date(),
    avatar: adminInfo.value.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
  })
  scrollToBottom()
}

const switchToHuman = () => {
  if (chatMode.value === 'manual') return
  chatMode.value = 'manual'
  isHuman.value = true
  loadChatHistory()
  startManualRealtime()
}

const send = async () => {
  if (!input.value.trim() && !selectedImage.value) return

  const question = input.value

  // 添加用户消息
  const userMessage = {
    id: messages.value.length + 1,
    role: 'user',
    content: question,
    createTime: new Date(),
    avatar: userInfo.value.avatar
  }
  
  if (selectedImage.value) {
    userMessage.image = selectedImage.value
  }
  
  addMessage(userMessage)

  const tempImage = selectedImage.value
  selectedImage.value = ''
  fileList.value = []
  input.value = ''

  try {
    if (chatMode.value === 'manual') {
      // 人工客服
      await request.post('/chat/send', {
        sender: 1,
        content: question,
        msgType: 'text'
      })
      showToast('已发送给人工客服')
    } else if (chatMode.value === 'agent') {
      // Agent智能助手
      const res = await request.post('/agent/chat', {
        message: question
      })
      let reply = (res.data && res.data.reply) || '抱歉，我暂时无法处理您的请求。'
      const toolCalls = (res.data && res.data.tool_calls) || []
      if (toolCalls.length > 0) {
        const toolInfo = toolCalls.map(tc => {
          try {
            const result = JSON.parse(tc.result)
            if (result.result) return '✅ ' + result.result
            return ''
          } catch { return '' }
        }).filter(s => s).join('\n')
        if (toolInfo) reply = reply + '\n' + toolInfo
      }
      addMessage({
        id: messages.value.length + 1,
        role: 'ai',
        content: reply,
        createTime: new Date(),
        avatar: adminInfo.value.avatar
      })
    } else {
      // AI客服
      const res = await request.post('/ai/chat', { 
        question,
        imageUrl: tempImage 
      })
      addMessage({
        id: messages.value.length + 1,
        role: 'ai',
        content: res.data,
        createTime: new Date(),
        avatar: adminInfo.value.avatar
      })
    }
  } catch (error) {
    console.error('发送失败:', error)
    showToast(error.message || '发送失败，请重试')
  }
}

const afterRead = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.file)
    
    const res = await request.post('/common/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (res.code === 200 && res.data) {
      selectedImage.value = res.data.url
      showToast('图片上传成功')
    } else {
      showToast(res.message || '图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    showToast('图片上传失败')
  }
}

const removeImage = () => {
  selectedImage.value = ''
  fileList.value = []
}

const quickAsk = (question) => {
  input.value = question
  send()
}

const loadChatHistory = async () => {
  // Agent模式和AI客服模式不加载历史记录
  if (chatMode.value === 'agent' || chatMode.value === 'ai') {
    messages.value = []
    return
  }
  try {
    const res = await request.get('/chat/session')
    if (res.data && res.data.length > 0) {
      messages.value = res.data.map(msg => ({
        id: msg.id,
        role: msg.sender === 1 ? 'user' : msg.sender === 0 ? 'ai' : 'admin',
        content: msg.content,
        createTime: msg.createTime,
        avatar: msg.avatar || (msg.sender === 1 ? userInfo.value.avatar : adminInfo.value.avatar)
      }))
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
  }
}

const mergeManualMessage = (payload) => {
  if (!payload || payload.sender !== 2) return
  if (messages.value.some(item => item.id === payload.id)) return
  addMessage({
    id: payload.id,
    role: 'admin',
    content: payload.content,
    createTime: payload.createTime || new Date(),
    avatar: payload.avatar || adminInfo.value.avatar
  })
}

const startManualRealtime = () => {
  if (!chatSocket || chatSocket.readyState === WebSocket.CLOSED || chatSocket.readyState === WebSocket.CLOSING) {
    chatSocket = connectChat((message) => {
      if (message.event !== 'chat.message' || chatMode.value !== 'manual') return
      mergeManualMessage(message.payload || {})
    })
  }
  if (!chatPollingTimer) {
    chatPollingTimer = setInterval(() => {
      if (chatMode.value === 'manual') {
        loadChatHistory()
      }
    }, 2000)
  }
}

const stopManualRealtime = () => {
  if (chatPollingTimer) {
    clearInterval(chatPollingTimer)
    chatPollingTimer = null
  }
}

onMounted(() => {
  loadUserInfo()
  loadAdminInfo()
  loadChatHistory()
})

onBeforeUnmount(() => {
  stopManualRealtime()
  if (chatSocket) {
    chatSocket.close()
    chatSocket = null
  }
})
</script>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-page);
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #1E293B 0%, #334155 100%);
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-back {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-header h2 {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.btn-switch {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-switch:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.btn-switch.active {
  background: rgba(255, 255, 255, 0.9);
  color: #304156;
  border-color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.chat-content {
  flex: 1;
  overflow: hidden;
}

.message-list {
  height: 100%;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message.left {
  align-self: flex-start;
}

.message.right {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  background-color: #f0f0f0;
}

.message-content-wrapper {
  display: flex;
  flex-direction: column;
  max-width: 100%;
}

.message.left .message-content-wrapper {
  align-items: flex-start;
}

.message.right .message-content-wrapper {
  align-items: flex-end;
}

.message-bubble {
  padding: 14px 18px;
  border-radius: 16px;
  max-width: 100%;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  display: inline-block;
  width: fit-content;
}

.message.left .message-bubble {
  background: white;
  border-bottom-left-radius: 4px;
}

.message.right .message-bubble {
  background: var(--primary-gradient);
  color: white;
  border-bottom-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.25);
}

.message-content {
  font-size: 15px;
  line-height: 1.6;
  word-break: break-word;
}

.message-image {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  margin-bottom: 8px;
  object-fit: cover;
}

.message-content a.chat-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
  border-bottom: 1px solid #409eff;
  padding-bottom: 2px;
  transition: all 0.3s;
}

.message-content a.chat-link:hover {
  color: #66b1ff;
  border-bottom-color: #66b1ff;
  text-decoration: none;
}

.message.right .message-content a.chat-link {
  color: #e6f7ff;
  border-bottom-color: #e6f7ff;
}

.message.right .message-content a.chat-link:hover {
  color: white;
  border-bottom-color: white;
}

.message-time {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.5);
  margin-top: 6px;
}

.input-area {
  background: white;
  border-top: 1px solid #F1F5F9;
  padding: 20px 24px;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  max-width: 1200px;
  margin: 0 auto;
  align-items: flex-end;
}

.image-preview {
  position: relative;
  display: flex;
  align-items: center;
}

.preview-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e4e7ed;
}

.remove-img {
  position: absolute;
  top: -8px;
  right: -8px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  padding: 4px;
  cursor: pointer;
}

.input-actions {
  display: flex;
  gap: 12px;
  flex: 1;
  align-items: center;
}

.upload-btn {
  cursor: pointer;
  padding: 8px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s;
}

.upload-btn:hover {
  border-color: #409eff;
}

.upload-btn :deep(.van-uploader__preview) {
  display: none !important;
}

.chat-input {
  flex: 1;
  padding: 14px 20px;
  border: 2px solid #E2E8F0;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-family: inherit;
  transition: all 0.2s;
}

.chat-input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(79, 110, 247, 0.1);
}

.btn-send {
  background: var(--primary-gradient);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: var(--radius-md);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  white-space: nowrap;
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.3);
}

.btn-send:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(79, 110, 247, 0.4);
}

.quick-questions {
  max-width: 1200px;
  margin: 20px auto 0;
}

.quick-title {
  font-size: 14px;
  color: #909399;
  margin: 0 0 12px;
}

.quick-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.quick-tag {
  padding: 8px 16px;
  background: var(--primary-bg);
  color: var(--primary);
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.quick-tag:hover {
  background: rgba(79, 110, 247, 0.12);
  border-color: rgba(79, 110, 247, 0.2);
}
</style>
