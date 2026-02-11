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
    loading: false,
    hasLoaded: false
  },

  onLoad() {
    console.log('页面加载')
    this.loadAllOrders()
  },

  onShow() {
    console.log('页面显示')
    // 从详情页返回时刷新数据
    this.loadAllOrders()
  },

  switchTab(e) {
    const index = e.currentTarget.dataset.index
    console.log('切换标签页，当前索引:', index, '之前的索引:', this.data.activeTab)
    this.setData({ activeTab: index })
    this.filterOrders()
  },

  async loadAllOrders() {
    try {
      this.setData({ loading: true })
      wx.showLoading({ title: '加载中...' })

      // 并行加载进行中和已完成的工单
      const [inProgressRes, completedRes] = await Promise.all([
        api.getWorkerRepairs({ page: 1, size: 100 }),
        api.getWorkerCompletedRepairs({ page: 1, size: 100 })
      ])

      let allOrders = []

      // 处理图片的辅助函数
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
          console.error('解析图片失败:', e)
          return []
        }
      }

      // 处理进行中的工单
      if (inProgressRes.data?.records) {
        console.log('进行中工单原始数据:', inProgressRes.data.records.map(r => ({
          id: r.id,
          status: r.status,
          allKeys: Object.keys(r)
        })))
        const inProgressOrders = inProgressRes.data.records.map(order => ({
          ...order,
          images: processImages(order.images)
        }))
        allOrders = allOrders.concat(inProgressOrders)
      }

      // 处理已完成的工单
      if (completedRes.data?.records) {
        console.log('已完成工单原始数据:', completedRes.data.records.map(r => ({
          id: r.id,
          status: r.status,
          allKeys: Object.keys(r)
        })))
        const completedOrders = completedRes.data.records.map(order => ({
          ...order,
          images: processImages(order.images)
        }))
        allOrders = allOrders.concat(completedOrders)
      }

      // 去重：根据工单ID去重
      const uniqueOrders = []
      const orderIds = new Set()
      allOrders.forEach(order => {
        if (!orderIds.has(order.id)) {
          orderIds.add(order.id)
          uniqueOrders.push(order)
        }
      })
      allOrders = uniqueOrders

      console.log('去重后的所有工单:', allOrders.map(o => ({
        id: o.id,
        status: o.status,
        statusType: typeof o.status,
        statusEquals1: o.status == 1,
        statusEquals2: o.status == 2,
        statusEquals3: o.status == 3,
        content: o.content?.substring(0, 20)
      })))

      // 计算统计
      const stats = {
        pending: allOrders.filter(o => parseInt(o.status) === 1).length,
        inProgress: allOrders.filter(o => parseInt(o.status) === 2).length,
        completed: allOrders.filter(o => parseInt(o.status) === 3).length
      }
      console.log('统计结果:', stats)

      console.log('准备setData，allOrders数量:', allOrders.length)

      this.setData({ allOrders, stats }, () => {
        console.log('setData完成，检查data中的allOrders:', this.data.allOrders.length)
        console.log('data中第一个工单:', this.data.allOrders[0])
      })
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
    console.log('筛选工单，当前标签:', activeTab, '总工单数:', allOrders.length)

    let filteredOrders = []

    // 修复：确保是数字相加
    const targetStatus = parseInt(activeTab) + 1 // activeTab 0->status 1, 1->status 2, 2->status 3
    console.log('目标status:', targetStatus, '类型:', typeof targetStatus)

    filteredOrders = allOrders.filter(o => {
      const orderStatus = parseInt(o.status) // 确保工单状态也是数字
      console.log('工单ID:', o.id, 'status:', orderStatus, '类型:', typeof orderStatus, '是否匹配:', orderStatus === targetStatus)
      return orderStatus === targetStatus
    })

    console.log('筛选后的工单数:', filteredOrders.length, '工单:', filteredOrders.map(o => ({ id: o.id, status: o.status, statusType: typeof o.status })))
    this.setData({ orderList: filteredOrders })
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/repairer-detail/repairer-detail?id=${id}`
    })
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
