// pages/repairer-home/repairer-home.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    activeTab: 0,
    stats: {
      pending: 0,
      inProgress: 0,
      completed: 0
    },
    allOrders: [],
    orderList: [],
    loading: false
  },

  onLoad() {
    this.loadAllOrders()
  },

  onShow() {
    this.loadAllOrders()
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index
    this.setData({ activeTab: index })
    this.filterOrders()
  },

  async loadAllOrders() {
    try {
      this.setData({ loading: true })
      wx.showLoading({ title: '加载中...' })

      const [inProgressRes, completedRes] = await Promise.all([
        api.getWorkerRepairs({ page: 1, size: 100 }),
        api.getWorkerCompletedRepairs({ page: 1, size: 100 })
      ])

      let allOrders = []

      const processImages = (images) => {
        if (!images) return []
        try {
          let imageArray = []
          if (typeof images === 'string') {
            imageArray = JSON.parse(images)
          } else if (Array.isArray(images)) {
            imageArray = images
          }
          return imageArray
            .filter(img => img && img !== 'null' && img !== '[null]')
            .map(img => formatImages(img))
        } catch (e) {
          return []
        }
      }

      if (inProgressRes.data?.records) {
        const inProgressOrders = inProgressRes.data.records.map(order => ({
          ...order,
          images: processImages(order.images)
        }))
        allOrders = allOrders.concat(inProgressOrders)
      }

      if (completedRes.data?.records) {
        const completedOrders = completedRes.data.records.map(order => ({
          ...order,
          images: processImages(order.images)
        }))
        allOrders = allOrders.concat(completedOrders)
      }

      // 去重
      const uniqueOrders = []
      const orderIds = new Set()
      allOrders.forEach(order => {
        if (!orderIds.has(order.id)) {
          orderIds.add(order.id)
          uniqueOrders.push(order)
        }
      })
      allOrders = uniqueOrders

      const stats = {
        pending: allOrders.filter(o => parseInt(o.status) === 1).length,
        inProgress: allOrders.filter(o => parseInt(o.status) === 2).length,
        completed: allOrders.filter(o => parseInt(o.status) === 3).length
      }

      this.setData({ allOrders, stats })
      this.filterOrders()
    } catch (err) {
      console.error('加载工单失败:', err)
      wx.showToast({
        title: '加载工单失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
      wx.hideLoading()
    }
  },

  filterOrders() {
    const { activeTab, allOrders } = this.data
    const targetStatus = parseInt(activeTab) + 1
    const filteredOrders = allOrders.filter(o => parseInt(o.status) === targetStatus)
    this.setData({ orderList: filteredOrders })
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/repairer-detail/repairer-detail?id=${id}`
    })
  },

  refreshOrders() {
    this.loadAllOrders()
  },

  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearLoginInfo()
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            })
          }, 1500)
        }
      }
    })
  }
})
