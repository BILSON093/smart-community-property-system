// pages/chat/chat.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    messageList: [],
    inputText: '',
    loading: false,
    userAvatar: '/images/avatar-default.png',
    aiAvatar: '/images/ai-avatar.png',
    chatMode: 'agent', // 'agent'、'ai' 或 'manual'
    scrollToView: '',
    selectedImage: '', // 已选中的图片
    imageReady: false // 图片是否已准备好
  },

  onLoad() {
    if (app.globalData.userInfo && app.globalData.userInfo.avatar) {
      this.setData({ userAvatar: formatImages(app.globalData.userInfo.avatar) })
    }
    this.loadAdminInfo()
    this.loadChatHistory()
  },

  loadAdminInfo() {
    api.getAdminInfo().then(res => {
      const admin = res.data || {}
      this.setData({ aiAvatar: formatImages(admin.avatar || '/images/ai-avatar.png') })
    }).catch(() => {
      this.setData({ aiAvatar: '/images/ai-avatar.png' })
    })
  },

  loadChatHistory() {
    // Agent模式和AI客服模式不加载历史记录
    if (this.data.chatMode === 'agent' || this.data.chatMode === 'ai') {
      this.setData({ messageList: [] })
      return
    }
    api.getChatHistory().then(res => {
      // sender: 1 是用户，2 是管理员/客服
      const messages = (res.data || []).map(item => ({
        id: item.id || Date.now(),
        sender: item.sender === 1 ? 0 : 1, // 0: 用户, 1: 客服
        content: item.msgType === 'image' ? formatImages(item.content) : (item.content || ''),
        type: item.msgType || 'text',
        avatar: item.avatar
      }))
      this.setData({ messageList: messages })
      this.scrollToBottom()
    }).catch(() => {
      this.setData({ messageList: [] })
    })
  },

  scrollToBottom() {
    if (this.data.messageList.length > 0) {
      const lastId = this.data.messageList[this.data.messageList.length - 1].id
      this.setData({ scrollToView: `msg-${lastId}` })
    }
  },

  onInputChange(e) {
    this.setData({ inputText: e.detail.value })
  },

  // 选择图片
  chooseImage() {
    const that = this
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success(res) {
        const tempFilePath = res.tempFiles[0].tempFilePath
        that.uploadImage(tempFilePath)
      },
      fail(err) {
        console.error('选择图片失败:', err)
      }
    })
  },

  // 上传图片
  uploadImage(filePath) {
    const that = this
    wx.showLoading({ title: '上传中...' })

    api.uploadFile(filePath).then(res => {
      wx.hideLoading()
      const imageUrl = res.url

      // 保存图片到数据，不立即发送，让用户可以添加文字
      that.setData({
        selectedImage: formatImages(imageUrl),
        imageReady: true
      })

      wx.showToast({
        title: '图片已选择，请输入问题后发送',
        icon: 'none',
        duration: 2000
      })
    }).catch(err => {
      wx.hideLoading()
      wx.showToast({
        title: '上传失败',
        icon: 'none'
      })
      console.error('上传失败:', err)
    })
  },

  // 发送图片消息
  sendImageMessage(imageUrl) {
    const { messageList, chatMode } = this.data

    // 添加用户图片消息
    const userMessage = {
      id: Date.now(),
      sender: 0,
      content: imageUrl,
      type: 'image'
    }

    this.setData({
      messageList: [...messageList, userMessage],
      loading: true,
      scrollToView: `msg-${userMessage.id}`
    })

    // AI 模式调用 AI 接口
    if (chatMode === 'ai') {
      api.aiChat({ question: '', imageUrl: imageUrl }).then(res => {
        const aiReply = res.data || '抱歉，我暂时无法识别这张图片，请联系人工客服。'
        const aiMessage = {
          id: Date.now(),
          sender: 1,
          content: aiReply,
          type: 'text'
        }
        this.setData({
          messageList: [...this.data.messageList, aiMessage],
          loading: false,
          scrollToView: `msg-${aiMessage.id}`
        })
      }).catch(() => {
        this.setData({ loading: false })
        wx.showToast({
          title: '发送失败',
          icon: 'none'
        })
      })
    } else if (chatMode === 'agent') {
      api.agentChat({ message: '[图片]' }).then(res => {
        const reply = (res.data && res.data.reply) || '抱歉，我暂时无法处理您的请求。'
        const aiMessage = {
          id: Date.now(),
          sender: 1,
          content: reply,
          type: 'text'
        }
        this.setData({
          messageList: [...this.data.messageList, aiMessage],
          loading: false,
          scrollToView: `msg-${aiMessage.id}`
        })
      }).catch(() => {
        this.setData({ loading: false })
        wx.showToast({ title: '发送失败', icon: 'none' })
      })
    } else {
      // 人工客服模式 - 保存到数据库
      api.sendChatMessage({
        sender: 1,
        content: imageUrl,
        msgType: 'image'
      }).then(() => {
        this.setData({ loading: false })
      }).catch(() => {
        this.setData({ loading: false })
        wx.showToast({
          title: '发送失败',
          icon: 'none'
        })
      })
    }
  },

  sendMessage() {
    const { inputText, messageList, chatMode, selectedImage } = this.data

    // 没有文字也没有图片时提示
    if ((!inputText || inputText.trim().length === 0) && !selectedImage) {
      wx.showToast({
        title: '请输入您的问题或选择图片',
        icon: 'none'
      })
      return
    }

    // 如果有图片，先显示图片消息
    if (selectedImage) {
      const imageMessage = {
        id: Date.now(),
        sender: 0,
        content: selectedImage,
        type: 'image'
      }
      this.setData({
        messageList: [...messageList, imageMessage],
        scrollToView: `msg-${imageMessage.id}`
      })
    }

    // 添加文字消息
    if (inputText && inputText.trim().length > 0) {
      const textMessage = {
        id: Date.now() + 1,
        sender: 0,
        content: inputText,
        type: 'text'
      }
      this.setData({
        messageList: selectedImage ? [...this.data.messageList, textMessage] : [...messageList, textMessage],
        inputText: '',
        loading: true,
        scrollToView: `msg-${textMessage.id}`
      })
    } else {
      this.setData({ loading: true })
    }

    // 根据模式发送消息
    if (chatMode === 'ai') {
      // AI 模式 - 提取相对路径，不包含域名
      let imagePath = selectedImage || ''
      if (imagePath.startsWith('http')) {
        // 提取 /upload/xxx.jpg 部分
        const match = imagePath.match(/\/upload\/[^?]+/)
        if (match) {
          imagePath = match[0]
        }
      }

      api.aiChat({
        question: inputText || '',
        imageUrl: imagePath
      }).then(res => {
        const aiReply = res.data || '抱歉，我暂时无法理解您的问题，请联系人工客服。'
        const aiMessage = {
          id: Date.now(),
          sender: 1,
          content: aiReply,
          type: 'text'
        }
        this.setData({
          messageList: [...this.data.messageList, aiMessage],
          loading: false,
          selectedImage: '',
          imageReady: false,
          scrollToView: `msg-${aiMessage.id}`
        })
      }).catch(() => {
        this.setData({
          loading: false,
          selectedImage: '',
          imageReady: false
        })
        wx.showToast({
          title: '消息发送失败',
          icon: 'none'
        })
      })
    } else if (chatMode === 'agent') {
      // Agent模式 - 只发送文字
      api.agentChat({
        message: inputText || ''
      }).then(res => {
        const reply = (res.data && res.data.reply) || '抱歉，我暂时无法处理您的请求。'
        const toolCalls = (res.data && res.data.tool_calls) || []
        let content = reply
        if (toolCalls.length > 0) {
          const toolInfo = toolCalls.map(tc => {
            try {
              const result = JSON.parse(tc.result)
              if (result.result) return '✅ ' + result.result
              return ''
            } catch { return '' }
          }).filter(s => s).join('\n')
          if (toolInfo) content = content + '\n' + toolInfo
        }
        const aiMessage = {
          id: Date.now(),
          sender: 1,
          content: content,
          type: 'text'
        }
        this.setData({
          messageList: [...this.data.messageList, aiMessage],
          loading: false,
          selectedImage: '',
          imageReady: false,
          scrollToView: `msg-${aiMessage.id}`
        })
      }).catch(() => {
        this.setData({
          loading: false,
          selectedImage: '',
          imageReady: false
        })
        wx.showToast({ title: '消息发送失败', icon: 'none' })
      })
    } else {
      // 人工客服模式 - 保存到数据库
      const promises = []

      // 发送图片
      if (selectedImage) {
        promises.push(api.sendChatMessage({
          sender: 1,
          content: selectedImage,
          msgType: 'image'
        }))
      }

      // 发送文字
      if (inputText && inputText.trim().length > 0) {
        promises.push(api.sendChatMessage({
          sender: 1,
          content: inputText,
          msgType: 'text'
        }))
      }

      Promise.all(promises).then(() => {
        this.setData({
          loading: false,
          selectedImage: '',
          imageReady: false
        })
      }).catch(() => {
        this.setData({
          loading: false,
          selectedImage: '',
          imageReady: false
        })
        wx.showToast({
          title: '消息发送失败',
          icon: 'none'
        })
      })
    }
  },

  // 预览图片
  previewImage(e) {
    const src = e.currentTarget.dataset.src
    wx.previewImage({
      urls: [src]
    })
  },

  // 移除已选中的图片
  removeImage() {
    this.setData({
      selectedImage: '',
      imageReady: false
    })
  },

  switchChatMode() {
    const { chatMode } = this.data
    const modes = ['agent', 'ai', 'manual']
    const modeNames = { agent: '智能助手', ai: 'AI客服', manual: '人工客服' }
    const currentIndex = modes.indexOf(chatMode)
    const newMode = modes[(currentIndex + 1) % modes.length]
    const modeName = modeNames[newMode]

    this.setData({ chatMode: newMode })

    if (newMode === 'manual') {
      this.loadChatHistory()
    } else {
      this.setData({ messageList: [] })
    }

    wx.showToast({
      title: `已切换到${modeName}`,
      icon: 'none'
    })
  }
})
