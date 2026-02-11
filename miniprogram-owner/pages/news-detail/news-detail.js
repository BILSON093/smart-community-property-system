// pages/news-detail/news-detail.js
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    notice: null,
    loading: false
  },

  onLoad(options) {
    if (options.id) {
      this.loadNoticeDetail(options.id)
    }
  },

  // 加载通知详情
  loadNoticeDetail(id) {
    this.setData({ loading: true })
    api.getNoticeDetail(id).then(res => {
      const data = res.data || {}
      this.setData({
        notice: {
          id: data.id,
          title: data.title || '',
          content: data.content || '',
          type: data.type || 0,
          images: this.formatImages(data.image),
          attachment: this.formatAttachments(data.attachment),
          publishTime: formatDate(data.publishTime, 'YYYY-MM-DD HH:mm')
        },
        loading: false
      })
    }).catch(() => {
      this.setData({
        notice: null,
        loading: false
      })
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    })
  },

  // 格式化图片数组（处理逗号分隔的情况）
  formatImages(image) {
    if (!image) return []

    // 如果是字符串，检查是否有逗号
    if (typeof image === 'string') {
      const trimmed = image.trim()
      // 如果包含逗号，分割成数组
      if (trimmed.includes(',')) {
        return trimmed.split(',').map(part => {
          const item = part.trim()
          if (item.startsWith('http://') || item.startsWith('https://')) {
            return item
          }
          return formatImages(item)
        })
      }
      // 单张图片
      if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
        return [trimmed]
      }
      return [formatImages(trimmed)]
    }

    // 如果已经是数组
    if (Array.isArray(image)) {
      return image.map(item => {
        const trimmed = item ? item.trim() : ''
        if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
          return trimmed
        }
        return formatImages(item)
      })
    }

    return []
  },

  // 格式化附件
  formatAttachments(attachment) {
    if (!attachment) return []

    // 如果是字符串，尝试解析为数组
    if (typeof attachment === 'string') {
      try {
        // 可能是用逗号分隔的字符串
        const parts = attachment.split(',')
        return parts.map(part => {
          const trimmed = part.trim()
          // 先判断是否已经是完整URL，如果是就不调用formatImages
          if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
            return trimmed
          }
          return formatImages(trimmed)
        })
      } catch (e) {
        // 解析失败，直接格式化整个字符串
        const trimmed = attachment.trim()
        if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
          return [trimmed]
        }
        return [formatImages(attachment)]
      }
    }

    // 如果已经是数组
    if (Array.isArray(attachment)) {
      return attachment.map(item => {
        const trimmed = item ? item.trim() : ''
        if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
          return trimmed
        }
        return formatImages(item)
      })
    }

    return []
  },

  // 预览图片
  previewImage(e) {
    const current = e.currentTarget.dataset.current
    const urls = e.currentTarget.dataset.urls
    wx.previewImage({
      current: current,
      urls: urls
    })
  },

  // 预览附件
  previewAttachment(e) {
    const url = e.currentTarget.dataset.url

    // 判断文件类型
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp', '.bmp']
    const isImage = imageExtensions.some(ext => url.toLowerCase().endsWith(ext))

    if (isImage) {
      // 图片类型使用预览
      wx.previewImage({
        current: url,
        urls: [url]
      })
    } else {
      // 非图片类型提示下载
      wx.showModal({
        title: '下载附件',
        content: '是否下载此附件？',
        success: (res) => {
          if (res.confirm) {
            wx.downloadFile({
              url: url,
              success: (downloadRes) => {
                if (downloadRes.statusCode === 200) {
                  wx.openDocument({
                    filePath: downloadRes.tempFilePath,
                    showMenu: true,
                    success: () => {
                      console.log('打开文档成功')
                    },
                    fail: (err) => {
                      console.error('打开文档失败', err)
                      wx.showToast({
                        title: '打开文档失败',
                        icon: 'none'
                      })
                    }
                  })
                } else {
                  wx.showToast({
                    title: '下载失败',
                    icon: 'none'
                  })
                }
              },
              fail: (err) => {
                console.error('下载失败', err)
                wx.showToast({
                  title: '下载失败',
                  icon: 'none'
                })
              }
            })
          }
        }
      })
    }
  },

  // 阻止事件冒泡
  preventBubble() {
    // 空方法，用于阻止事件冒泡
  }
})
