<template>
  <div class="chat-container">
    <div class="chat-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          <van-icon name="arrow-left" size="20" color="white" />
          返回
        </button>
        <div class="header-title">
          <van-icon name="chat-o" size="28" color="#fff" />
          <h2>{{ isHuman ? '人工客服' : 'AI智能客服' }}</h2>
        </div>
      </div>
      <button @click="switchService" class="btn-switch">
        <van-icon :name="isHuman ? 'chat' : 'service'" size="18" />
        {{ isHuman ? '切换到AI' : '切换到人工' }}
      </button>
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
              <div class="message-content" v-html="formatContentWithLinks(msg.content)"></div>
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
      <div class="quick-questions" v-if="!isHuman">
        <p class="quick-title">常见问题：</p>
        <div class="quick-tags">
          <span
            v-for="(q, index) in quickQuestions"
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
import { ref, onMounted, nextTick } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'
import router from '@/router'

const isHuman = ref(false)
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
const messageList = ref(null)
const userInfo = ref({ avatar: '', name: '' })
const adminInfo = ref({ avatar: '', name: '' })

const goBack = () => {
  router.back()
}

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

const formatContentWithLinks = (content) => {
  if (!content) return ''
  
  // 替换Markdown链接为HTML链接
  let formattedContent = content.replace(/\[([^\]]+)\]\(([^)]+)\)/g, (match, text, url) => {
    // 处理内部路由链接
    if (url.startsWith('/')) {
      // 映射AI生成的路径到实际路由
      let routePath = url
      if (url.includes('/pages/activity/')) {
        routePath = '/activity'
      } else if (url.includes('/pages/fee/')) {
        routePath = '/pay'
      } else if (url.includes('/pages/repair/')) {
        routePath = '/repair'
      } else if (url.includes('/pages/report/')) {
        routePath = '/repair'
      } else if (url.includes('/pages/forum/')) {
        routePath = '/forum'
      } else if (url.includes('/pages/parking/')) {
        routePath = '/pay'
      }
      
      return `<a href="#" class="chat-link" data-path="${routePath}">${text}</a>`
    }
    // 外部链接
    return `<a href="${url}" target="_blank" class="chat-link">${text}</a>`
  })
  
  return formattedContent
}

const switchService = () => {
  isHuman.value = !isHuman.value
  showToast(isHuman.value ? '已切换到人工客服' : '已切换到AI客服')
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
    if (isHuman.value) {
      // 人工客服
      await request.post('/chat/send', {
        sender: 1,
        content: question,
        msgType: 'text'
      })
      showToast('已发送给人工客服')
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
    } else {
      // 添加欢迎消息
      messages.value.push({
        id: 1,
        role: 'ai',
        content: '您好，我是智慧社区的智能客服，请问有什么可以帮助您？',
        createTime: new Date(),
        avatar: adminInfo.value.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
      })
      scrollToBottom()
    }
  } catch (error) {
    console.error('加载聊天记录失败:', error)
    // 加载失败时显示欢迎消息
    messages.value.push({
      id: 1,
      role: 'ai',
      content: '您好，我是智慧社区的智能客服，请问有什么可以帮助您？',
      createTime: new Date(),
      avatar: adminInfo.value.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    })
    scrollToBottom()
  }
}

onMounted(() => {
  // 加载用户信息和管理员信息
  loadUserInfo()
  loadAdminInfo()
  
  // 加载聊天记录
  loadChatHistory()
  
  // 添加点击事件监听器
  document.addEventListener('click', (e) => {
    if (e.target.classList.contains('chat-link')) {
      e.preventDefault()
      const path = e.target.dataset.path
      if (path) {
        router.push(path)
      }
    }
  })
})
</script>

<style scoped>
.chat-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #304156 0%, #409EFF 100%);
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-back:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
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
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-switch:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
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
  padding: 16px 20px;
  border-radius: 16px;
  max-width: 100%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: inline-block;
  width: fit-content;
}

.message.left .message-bubble {
  background: white;
  border-bottom-left-radius: 4px;
}

.message.right .message-bubble {
  background: linear-gradient(135deg, #304156 0%, #409EFF 100%);
  color: white;
  border-bottom-right-radius: 4px;
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
  border-top: 1px solid #e4e7ed;
  padding: 20px 24px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
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
  border: 2px solid #dcdfe6;
  border-radius: 8px;
  font-size: 15px;
  font-family: inherit;
  transition: all 0.3s;
}

.chat-input:focus {
  outline: none;
  border-color: #409eff;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
}

.btn-send {
  background: linear-gradient(135deg, #304156 0%, #409EFF 100%);
  color: white;
  border: none;
  padding: 14px 32px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
  white-space: nowrap;
}

.btn-send:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
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
  background: #f0f9ff;
  color: #409eff;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.quick-tag:hover {
  background: #e6f7ff;
  border-color: #409eff;
  transform: translateY(-2px);
}
</style>
