// pages/profile/profile.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    userInfo: null
  },

  onLoad() {
    this.loadUserInfo()
  },

  onShow() {
    // 每次显示时刷新用户信息
    this.loadUserInfo()
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo
    if (!userInfo) {
      this.setData({ userInfo: null })
      return
    }

    // 调用接口获取完整用户信息（包含地址信息）
    api.getUserInfo().then(res => {
      const fullUserInfo = res.data || {}
      // 更新全局用户信息
      app.globalData.userInfo = { ...userInfo, ...fullUserInfo }
      const processedUserInfo = {
        ...fullUserInfo,
        avatar: formatImages(fullUserInfo.avatar)
      }
      this.setData({ userInfo: processedUserInfo })
    }).catch(() => {
      // 失败时使用缓存信息
      const processedUserInfo = {
        ...userInfo,
        avatar: formatImages(userInfo.avatar)
      }
      this.setData({ userInfo: processedUserInfo })
    })
  },

  goToLogin() {
    wx.navigateTo({
      url: '/pages/login/login'
    })
  },

  goToEditProfile() {
    wx.navigateTo({
      url: '/pages/edit-profile/edit-profile'
    })
  },

  goToRepair() {
    wx.navigateTo({
      url: '/pages/repair/repair'
    })
  },

  goToPay() {
    wx.navigateTo({
      url: '/pages/pay/pay'
    })
  },

  goToForum() {
    wx.switchTab({
      url: '/pages/forum/forum'
    })
  },

  goToFeedback() {
    wx.navigateTo({
      url: '/pages/feedback/feedback'
    })
  },

  changePassword() {
    wx.navigateTo({
      url: '/pages/change-password/change-password'
    })
  },

  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          app.clearLoginInfo()
          this.setData({ userInfo: null })
          wx.showToast({
            title: '已退出登录',
            icon: 'success'
          })
        }
      }
    })
  }
})
