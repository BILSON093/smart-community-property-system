// pages/login/login.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    username: '',
    password: '',
    showPassword: false,
    loginType: 1 // 1=业主, 2=维修员
  },

  onLoad() {
    // 如果已登录，根据角色跳转到不同页面
    if (app.checkLogin()) {
      const userInfo = app.globalData.userInfo || wx.getStorageSync('userInfo')
      if (userInfo && userInfo.role === 2) {
        wx.reLaunch({
          url: '/pages/repairer-home/repairer-home'
        })
      } else {
        wx.switchTab({
          url: '/pages/index/index'
        })
      }
    }
  },

  onUsernameInput(e) {
    this.setData({
      username: e.detail.value
    })
  },

  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    })
  },

  onRoleSelect(e) {
    const type = parseInt(e.currentTarget.dataset.type)
    this.setData({
      loginType: type
    })
  },

  togglePassword() {
    console.log('togglePassword被调用，当前showPassword:', this.data.showPassword)
    this.setData({
      showPassword: !this.data.showPassword
    })
    console.log('切换后showPassword:', !this.data.showPassword)
  },

  handleLogin() {
    const { username, password } = this.data

    if (!username) {
      wx.showToast({
        title: '请输入账号',
        icon: 'none'
      })
      return
    }

    if (!password) {
      wx.showToast({
        title: '请输入密码',
        icon: 'none'
      })
      return
    }

    wx.showLoading({
      title: '登录中...'
    })

    const { loginType } = this.data

    api.login({ username, password, loginType }).then(res => {
      // 先关闭 loading
      try {
        wx.hideLoading()
      } catch (e) {
        // 忽略 hideLoading 错误
      }

      // 调试：打印返回的数据结构
      console.log('登录返回数据:', res)
      console.log('完整res:', JSON.stringify(res))
      console.log('res.data:', res.data)
      console.log('用户角色:', res.data?.role, '类型:', typeof res.data?.role)
      console.log('token:', res.data?.token)

      // 检查用户角色（0：管理员，1：业主，2：维修员）
      const userRole = res.data?.role
      console.log('角色判断:', userRole, '类型:', typeof userRole)

      // 保存 token
      const token = res.data?.token || res.token
      app.globalData.token = token
      wx.setStorageSync('token', token)

      // 保存基本用户信息
      const userInfo = {
        id: res.data?.userId || res.data?.id,
        userId: res.data?.userId || res.data?.id,
        username: res.data?.username,
        realName: res.data?.realName,
        phone: res.data?.phone,
        avatar: res.data?.avatar,
        role: res.data?.role
      }
      app.globalData.userInfo = userInfo
      wx.setStorageSync('userInfo', userInfo)

      console.log('保存的token:', token)
      console.log('保存的userInfo:', userInfo)
      console.log('用户角色:', userRole, '是否为维修员:', userRole === 2)

      wx.showToast({
        title: '登录成功',
        icon: 'success'
      })

      setTimeout(() => {
        // 根据角色跳转到不同页面
        if (userRole === 2) {
          // 维修员跳转到维修工作台
          console.log('跳转到维修工作台')
          wx.reLaunch({
            url: '/pages/repairer-home/repairer-home'
          })
        } else {
          // 业主和管理员跳转到首页
          console.log('跳转到首页')
          wx.switchTab({
            url: '/pages/index/index'
          })
        }
      }, 500)
    }).catch((err) => {
      // 错误已经在 request.js 中统一处理并关闭了 loading
      // 这里不需要再调用 hideLoading，避免重复调用导致错误
      // request.js 会显示 toast 提示
      console.log('登录失败:', err)
    })
  },

  goToRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    })
  }
})
