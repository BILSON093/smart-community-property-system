// pages/repair/repair.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatDate, formatRelativeTime } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

const STATUS_MAP = {
  0: '待处理',
  1: '已派单',
  2: '维修中',
  3: '已完成',
  4: '已取消'
}

const REPAIR_TYPES = ['水电维修', '家电维修', '管道疏通', '门窗维修', '其他']

Page({
  data: {
    repairList: [],
    filteredRepairs: [],
    activeTab: 'all',
    loading: false,
    isLoggedIn: false,
    showModal: false,
    form: {
      content: '',
      type: ''
    },
    fileList: [],
    submitting: false,
    repairTypes: REPAIR_TYPES,
    showTypePicker: false,
    typePickerIndex: -1
  },

  onLoad() {
    this.loadRepairs()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadRepairs()
  },

  loadRepairs() {
    const isLoggedIn = app.checkLogin()
    this.setData({ isLoggedIn })

    if (!isLoggedIn) {
      this.setData({
        repairList: [],
        filteredRepairs: [],
        loading: false
      })
      return
    }

    this.setData({ loading: true })
    api.getMyRepairs({ page: 1, pageSize: 50 }).then(res => {
      const repairList = (res.data.records || []).map(item => {
        let images = []
        if (item.images) {
          try {
            images = typeof item.images === 'string' ? JSON.parse(item.images) : item.images
            // 处理图片路径
            images = formatImages(images)
          } catch (e) {
            images = []
          }
        }

        return {
          ...item,
          statusText: STATUS_MAP[item.status] || '未知',
          createTime: formatRelativeTime(item.createTime),
          images
        }
      })

      this.setData({
        repairList,
        filteredRepairs: repairList,
        loading: false
      })
    }).catch(() => {
      this.setData({
        repairList: [],
        filteredRepairs: [],
        loading: false
      })
    })
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
    this.filterRepairs()
  },

  filterRepairs() {
    const { repairList, activeTab } = this.data
    let filtered = repairList

    switch (activeTab) {
      case 'pending':
        filtered = repairList.filter(item => item.status === 0)
        break
      case 'processing':
        filtered = repairList.filter(item => item.status === 1 || item.status === 2)
        break
      case 'completed':
        filtered = repairList.filter(item => item.status === 3)
        break
      case 'all':
      default:
        filtered = repairList
        break
    }

    this.setData({ filteredRepairs: filtered })
  },

  viewDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/repair-detail/repair-detail?id=${id}`
    })
  },

  // 显示添加报修弹窗
  showAddModal() {
    if (!app.checkLogin()) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      setTimeout(() => {
        wx.navigateTo({
          url: '/pages/login/login'
        })
      }, 1500)
      return
    }

    this.setData({
      showModal: true,
      form: { content: '', type: '' },
      fileList: [],
      typePickerIndex: -1
    })
  },

  // 选择报修分类
  onTypeChange(e) {
    const index = parseInt(e.detail.value)
    this.setData({
      typePickerIndex: index,
      'form.type': REPAIR_TYPES[index]
    })
  },

  // 关闭弹窗
  closeModal() {
    this.setData({
      showModal: false
    })
  },

  // 阻止事件冒泡
  preventBubble() {
    // 空方法，仅用于阻止点击事件冒泡
  },

  // 输入故障描述
  onContentInput(e) {
    this.setData({
      'form.content': e.detail.value
    })
  },

  // 选择图片
  chooseImage() {
    const { fileList } = this.data
    const count = 3 - fileList.length

    wx.chooseMedia({
      count,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      sizeType: ['compressed'],
      success: (res) => {
        const tempFiles = res.tempFiles.map(file => file.tempFilePath)
        this.setData({
          fileList: [...fileList, ...tempFiles]
        })
      }
    })
  },

  // 删除图片
  removeFile(e) {
    const index = e.currentTarget.dataset.index
    const { fileList } = this.data
    fileList.splice(index, 1)
    this.setData({
      fileList
    })
  },

  // 上传单张图片
  uploadImage(filePath) {
    return new Promise((resolve, reject) => {
      const token = wx.getStorageSync('token') || ''

      wx.uploadFile({
        url: `${app.globalData.baseURL}/common/upload`,
        filePath: filePath,
        name: 'file',
        header: {
          'Authorization': token ? `Bearer ${token}` : ''
        },
        success: (res) => {
          console.log('上传成功:', res.data)
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200 && data.data && data.data.url) {
              resolve(data.data.url)
            } else {
              console.error('上传失败，响应:', data)
              reject(new Error('上传失败'))
            }
          } catch (e) {
            console.error('解析响应失败:', e)
            reject(e)
          }
        },
        fail: (err) => {
          console.error('上传请求失败:', err)
          reject(err)
        }
      })
    })
  },

  // 提交报修
  submitRepair() {
    const { form, fileList } = this.data

    if (!form.content || form.content.trim().length === 0) {
      wx.showToast({
        title: '请输入故障描述',
        icon: 'none'
      })
      return
    }

    if (!app.checkLogin()) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    this.setData({ submitting: true })
    wx.showLoading({ title: '提交中...' })

    // 上传图片
    const uploadPromises = fileList.map(filePath => this.uploadImage(filePath))

    Promise.all(uploadPromises)
      .then(imageUrls => {
        const data = {
          content: form.content.trim(),
          images: JSON.stringify(imageUrls)
        }
        if (form.type) data.type = form.type
        return api.addRepair(data)
      })
      .then(() => {
        try {
          wx.hideLoading()
        } catch (e) {}
        wx.showToast({
          title: '提交成功',
          icon: 'success'
        })
        // 重置表单并关闭弹窗
        this.setData({
          form: { content: '' },
          fileList: [],
          submitting: false,
          showModal: false
        })
        // 刷新列表
        this.loadRepairs()
      })
      .catch((error) => {
        try {
          wx.hideLoading()
        } catch (e) {}
        this.setData({ submitting: false })
        if (error && error.message) {
          wx.showToast({
            title: error.message,
            icon: 'none'
          })
        }
      })
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url
    const urls = e.currentTarget.dataset.urls
    wx.previewImage({
      current: url,
      urls: urls
    })
  }
})
