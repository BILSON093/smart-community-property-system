// pages/register/register.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    username: '',
    password: '',
    confirmPassword: '',
    realName: '',
    phone: '',
    idCard: '',
    building: '',
    unit: '',
    room: '',
    showPassword: false
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value })
  },

  onRealNameInput(e) {
    this.setData({ realName: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onIdCardInput(e) {
    this.setData({ idCard: e.detail.value })
  },

  onBuildingInput(e) {
    this.setData({ building: e.detail.value })
  },

  onUnitInput(e) {
    this.setData({ unit: e.detail.value })
  },

  onRoomInput(e) {
    this.setData({ room: e.detail.value })
  },

  togglePassword() {
    this.setData({ showPassword: !this.data.showPassword })
  },

  validateForm() {
    const { username, password, confirmPassword, realName, phone, idCard, building, unit, room } = this.data

    if (!username) {
      wx.showToast({ title: '请输入用户名', icon: 'none' })
      return false
    }

    if (!password || password.length < 6 || password.length > 20) {
      wx.showToast({ title: '密码长度为6-20位', icon: 'none' })
      return false
    }

    if (password !== confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' })
      return false
    }

    if (!realName) {
      wx.showToast({ title: '请输入真实姓名', icon: 'none' })
      return false
    }

    if (!phone || !/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }

    if (!idCard || !/^\d{17}[\dXx]$/.test(idCard)) {
      wx.showToast({ title: '请输入正确的身份证号', icon: 'none' })
      return false
    }

    if (!building || !unit || !room) {
      wx.showToast({ title: '请填写完整地址信息', icon: 'none' })
      return false
    }

    return true
  },

  handleRegister() {
    if (!this.validateForm()) return

    const { username, password, realName, phone, idCard, building, unit, room } = this.data

    wx.showLoading({ title: '注册中...' })

    api.registerOwner({
      username,
      password,
      realName,
      phone,
      idCard,
      building,
      unit,
      room
    }).then(res => {
      wx.hideLoading()
      wx.showToast({
        title: '注册成功，请登录',
        icon: 'success'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }).catch(() => {
      wx.hideLoading()
    })
  },

  goToLogin() {
    wx.navigateBack()
  }
})
