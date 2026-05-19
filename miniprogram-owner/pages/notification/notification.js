// pages/notification/notification.js
const api = require('../../utils/api.js')
const { formatRelativeTime } = require('../../utils/util.js')

Page({
  data: {
    notifications: [],
    unreadCount: 0,
    loading: false,
    page: 1,
    hasMore: true
  },

  onLoad() {
    this.loadNotifications()
  },

  onShow() {
    this.loadNotifications()
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true })
    this.loadNotifications().then(() => {
      wx.stopPullDownRefresh()
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ page: this.data.page + 1 })
      this.loadMore()
    }
  },

  loadNotifications() {
    this.setData({ loading: true })
    return api.getMyNotifications({ page: 1, size: 20 }).then(res => {
      const records = (res.data && res.data.records) || []
      const notifications = records.map(item => ({
        ...item,
        createTime: formatRelativeTime(item.createTime)
      }))
      const unreadCount = notifications.filter(n => n.isRead === 0).length
      this.setData({
        notifications,
        unreadCount,
        loading: false,
        hasMore: records.length >= 20
      })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  loadMore() {
    this.setData({ loading: true })
    api.getMyNotifications({ page: this.data.page, size: 20 }).then(res => {
      const records = (res.data && res.data.records) || []
      const newItems = records.map(item => ({
        ...item,
        createTime: formatRelativeTime(item.createTime)
      }))
      this.setData({
        notifications: [...this.data.notifications, ...newItems],
        loading: false,
        hasMore: records.length >= 20
      })
    }).catch(() => {
      this.setData({ loading: false })
    })
  },

  markRead(e) {
    const id = e.currentTarget.dataset.id
    const index = e.currentTarget.dataset.index
    const item = this.data.notifications[index]
    if (item && item.isRead === 0) {
      api.markNotificationRead(id).then(() => {
        const key = `notifications[${index}].isRead`
        this.setData({
          [key]: 1,
          unreadCount: Math.max(0, this.data.unreadCount - 1)
        })
      }).catch(() => {})
    }
  },

  markAllRead() {
    api.markAllNotificationsRead().then(() => {
      const notifications = this.data.notifications.map(item => ({
        ...item,
        isRead: 1
      }))
      this.setData({
        notifications,
        unreadCount: 0
      })
      wx.showToast({ title: '全部已读', icon: 'success' })
    }).catch(() => {
      wx.showToast({ title: '操作失败', icon: 'none' })
    })
  }
})
