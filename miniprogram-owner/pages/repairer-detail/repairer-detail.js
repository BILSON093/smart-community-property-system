// pages/repairer-detail/repairer-detail.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    id: null,
    detail: null,
    images: []
  },

  onLoad(options) {
    const { id } = options
    if (id) {
      this.setData({ id })
      this.loadDetail()
    }
  },

  async loadDetail() {
    const { id } = this.data
    try {
      wx.showLoading({ title: '加载中...' })
      const res = await api.getRepairDetail(id)
      const detail = res.data || {}

      // 格式化图片路径
      let images = []
      if (detail.images) {
        try {
          // 尝试解析 JSON 字符串
          let imageArray = []
          if (typeof detail.images === 'string') {
            // 如果是 JSON 字符串，解析它
            imageArray = JSON.parse(detail.images)
          } else if (Array.isArray(detail.images)) {
            // 如果已经是数组，直接使用
            imageArray = detail.images
          }

          // 过滤掉 null 值并格式化路径
          images = imageArray
            .filter(img => img && img !== 'null' && img !== '[null]')
            .map(img => formatImages(img))
        } catch (e) {
          console.error('解析图片失败:', e, 'images:', detail.images)
          images = []
        }
      }

      console.log('处理后的图片列表:', images)
      this.setData({ detail, images })
    } catch (err) {
      console.error('加载详情失败:', err)
      wx.showToast({
        title: '加载详情失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 预览图片
  previewImage(e) {
    const { index } = e.currentTarget.dataset
    const { images } = this.data
    wx.previewImage({
      current: images[index],
      urls: images
    })
  },

  // 开始维修
  handleStart() {
    wx.showModal({
      title: '确认开始维修',
      content: '确定要开始维修此工单吗？',
      success: (res) => {
        if (res.confirm) {
          this.startRepair()
        }
      }
    })
  },

  async startRepair() {
    const { id } = this.data
    try {
      wx.showLoading({ title: '处理中...' })
      await api.startRepair(id)
      wx.showToast({
        title: '已开始维修',
        icon: 'success'
      })
      this.loadDetail()
    } catch (err) {
      console.error('开始维修失败:', err)
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 完成维修
  handleComplete() {
    wx.showModal({
      title: '确认完成维修',
      content: '确定要完成此工单吗？',
      success: (res) => {
        if (res.confirm) {
          this.completeRepair()
        }
      }
    })
  },

  async completeRepair() {
    const { id } = this.data
    try {
      wx.showLoading({ title: '处理中...' })
      await api.completeRepair(id)
      wx.showToast({
        title: '维修已完成',
        icon: 'success'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (err) {
      console.error('完成维修失败:', err)
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  },

  // 退单
  handleCancel() {
    wx.showModal({
      title: '确认退单',
      content: '确定要退单吗？',
      success: (res) => {
        if (res.confirm) {
          this.cancelRepair()
        }
      }
    })
  },

  async cancelRepair() {
    const { id } = this.data
    try {
      wx.showLoading({ title: '处理中...' })
      await api.cancelRepair(id)
      wx.showToast({
        title: '已退单',
        icon: 'success'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (err) {
      console.error('退单失败:', err)
      wx.showToast({
        title: '操作失败',
        icon: 'none'
      })
    } finally {
      wx.hideLoading()
    }
  }
})
