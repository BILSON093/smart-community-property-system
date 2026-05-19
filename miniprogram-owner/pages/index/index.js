// pages/index/index.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    carouselList: [],
    noticeList: [],
    activityList: [],
    pendingPayCount: 0,
    unreadCount: 0,
    loading: false
  },

  onLoad() {
    this.loadCarousel()
    this.loadNotice()
    this.loadActivity()
    this.loadPendingPayCount()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadNotice()
    this.loadActivity()
    this.loadPendingPayCount()
    this.loadUnreadCount()
  },

  // 加载轮播图
  loadCarousel() {
    api.getCarouselList().then(res => {
      const carouselList = (res.data || []).map(item => ({
        ...item,
        imageUrl: formatImages(item.imageUrl)
      }))
      this.setData({
        carouselList
      })
    }).catch(() => {
      this.setData({
        carouselList: []
      })
    })
  },

  // 加载通知
  loadNotice() {
    api.getNoticeList({ page: 1, pageSize: 3 }).then(res => {
      this.setData({
        noticeList: (res.data.records || []).map(item => ({
          ...item,
          publishTime: formatDate(item.publishTime, 'YYYY-MM-DD')
        }))
      })
    }).catch(() => {
      this.setData({ noticeList: [] })
    })
  },

  // 加载活动
  loadActivity() {
    api.getActivityList({ page: 1, pageSize: 3 }).then(res => {
      this.setData({
        activityList: (res.data.records || []).map(item => ({
          ...item,
          coverImage: formatImages(item.coverImage),
          startTime: formatDate(item.startTime, 'MM-DD')
        }))
      })
    }).catch(() => {
      this.setData({ activityList: [] })
    })
  },

  // 加载未读通知数
  loadUnreadCount() {
    if (!app.checkLogin()) {
      this.setData({ unreadCount: 0 })
      return
    }
    api.getUnreadCount().then(res => {
      if (res.code === 200 && res.data) {
        this.setData({ unreadCount: res.data.count || 0 })
      }
    }).catch(() => {
      this.setData({ unreadCount: 0 })
    })
  },

  // 加载待缴费数量
  loadPendingPayCount() {
    if (!app.checkLogin()) {
      this.setData({ pendingPayCount: 0 })
      return
    }

    api.getMyFees({ page: 1, pageSize: 50 }).then(res => {
      const fees = res.data.records || []
      // 统计未缴费的数量
      const pendingCount = fees.filter(fee => fee.status === 0).length
      this.setData({ pendingPayCount: pendingCount })
    }).catch(() => {
      this.setData({ pendingPayCount: 0 })
    })
  },

  // 导航方法
  goToRepair() {
    this.checkAuth(() => {
      wx.navigateTo({
        url: '/pages/repair/repair'
      })
    })
  },

  goToPay() {
    this.checkAuth(() => {
      wx.navigateTo({
        url: '/pages/pay/pay'
      })
    })
  },

  goToNews() {
    wx.navigateTo({
      url: '/pages/news/news'
    })
  },

  goToNotification() {
    this.checkAuth(() => {
      wx.navigateTo({
        url: '/pages/notification/notification'
      })
    })
  },

  goToActivity() {
    wx.navigateTo({
      url: '/pages/activity/activity'
    })
  },

  goToForum() {
    this.checkAuth(() => {
      wx.switchTab({
        url: '/pages/forum/forum'
      })
    })
  },

  goToFeedback() {
    this.checkAuth(() => {
      wx.navigateTo({
        url: '/pages/feedback/feedback'
      })
    })
  },

  goToChat() {
    this.checkAuth(() => {
      wx.switchTab({
        url: '/pages/chat/chat'
      })
    })
  },

  goToProfile() {
    this.checkAuth(() => {
      wx.switchTab({
        url: '/pages/profile/profile'
      })
    })
  },

  // 查看通知详情
  viewNoticeDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/news/news?id=${id}`
    })
  },

  // 查看活动详情
  viewActivityDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/activity-detail/activity-detail?id=${id}`
    })
  },

  // 检查登录状态
  checkAuth(callback) {
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
    callback && callback()
  }
})
