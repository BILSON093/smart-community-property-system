// pages/change-password/change-password.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  },

  onOldPasswordInput(e) {
    this.setData({
      oldPassword: e.detail.value
    })
  },

  onNewPasswordInput(e) {
    this.setData({
      newPassword: e.detail.value
    })
  },

  onConfirmPasswordInput(e) {
    this.setData({
      confirmPassword: e.detail.value
    })
  },

  submit() {
    const { oldPassword, newPassword, confirmPassword } = this.data

    if (!oldPassword) {
      wx.showToast({
        title: '请输入当前密码',
        icon: 'none'
      })
      return
    }

    if (!newPassword) {
      wx.showToast({
        title: '请输入新密码',
        icon: 'none'
      })
      return
    }

    if (newPassword.length < 6 || newPassword.length > 20) {
      wx.showToast({
        title: '密码长度为6-20位',
        icon: 'none'
      })
      return
    }

    if (!confirmPassword) {
      wx.showToast({
        title: '请确认新密码',
        icon: 'none'
      })
      return
    }

    if (newPassword !== confirmPassword) {
      wx.showToast({
        title: '两次密码不一致',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '修改中...' })
    api.changePassword({
      oldPassword: oldPassword,
      newPassword: newPassword
    }).then(() => {
      wx.hideLoading()
      wx.showToast({
        title: '密码修改成功',
        icon: 'success'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    }).catch(() => {
      wx.hideLoading()
    })
  }
})
