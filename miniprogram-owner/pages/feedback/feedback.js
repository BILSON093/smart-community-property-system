// pages/feedback/feedback.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatDate, formatRelativeTime } = require('../../utils/util.js')

Page({
  data: {
    feedbackList: [],
    feedbackContent: '',
    loading: false,
    isLoggedIn: false
  },

  onLoad() {
    this.loadFeedbacks()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadFeedbacks()
  },

  loadFeedbacks() {
    const isLoggedIn = app.checkLogin()
    this.setData({ isLoggedIn })

    if (!isLoggedIn) {
      this.setData({
        feedbackList: [],
        loading: false
      })
      return
    }

    // 获取当前用户ID
    const userInfo = app.globalData.userInfo || {}
    console.log('用户信息:', userInfo)
    const userId = userInfo.userId || userInfo.id

    if (!userId) {
      console.error('用户ID不存在:', userInfo)
      this.setData({
        feedbackList: [],
        loading: false
      })
      wx.showToast({
        title: '获取用户信息失败，请重新登录',
        icon: 'none'
      })
      return
    }

    console.log('加载留言列表，userId:', userId)

    this.setData({ loading: true })
    // 后端接口只需要 userId 参数，返回的是 List 不是 Page
    api.getMyFeedbacks({ userId }).then(res => {
      console.log('留言列表响应:', res)
      this.setData({
        feedbackList: (res.data || []).map(item => ({
          ...item,
          createTime: formatRelativeTime(item.createTime)
        })),
        loading: false
      })
    }).catch((err) => {
      console.error('加载留言失败:', err)
      this.setData({
        feedbackList: [],
        loading: false
      })
    })
  },

  onFeedbackInput(e) {
    this.setData({ feedbackContent: e.detail.value })
  },

  submitFeedback() {
    const { feedbackContent } = this.data
    if (!feedbackContent || feedbackContent.trim().length === 0) {
      wx.showToast({
        title: '请输入留言内容',
        icon: 'none'
      })
      return
    }

    const userInfo = app.globalData.userInfo
    if (!userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    const userId = userInfo.userId || userInfo.id
    if (!userId) {
      wx.showToast({
        title: '用户信息异常，请重新登录',
        icon: 'none'
      })
      return
    }

    console.log('提交留言，userId:', userId, 'userName:', userInfo.realName || userInfo.username)

    wx.showLoading({ title: '提交中...' })
    api.addFeedback({
      content: feedbackContent,
      userId: userId,
      userName: userInfo.realName || userInfo.username
    }).then(() => {
      try {
        wx.hideLoading()
      } catch (e) {
        // 忽略 hideLoading 错误
      }
      wx.showToast({
        title: '提交成功',
        icon: 'success'
      })
      this.setData({ feedbackContent: '' })
      // 刷新列表
      this.loadFeedbacks()
    }).catch((err) => {
      console.error('提交留言失败:', err)
      // request.js 中已经处理了错误和 loading，这里不需要再调用 hideLoading
    })
  }
})
