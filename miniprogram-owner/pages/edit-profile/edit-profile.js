// pages/edit-profile/edit-profile.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    formData: {
      username: '',
      realName: '',
      phone: '',
      idCard: '',
      building: '',
      unit: '',
      room: '',
      avatar: ''
    }
  },

  onLoad() {
    this.loadUserInfo()
  },

  loadUserInfo() {
    const userInfo = app.globalData.userInfo
    if (userInfo) {
      // 获取业主详细信息
      api.getUserInfo().then(res => {
        const data = res.data
        this.setData({
          formData: {
            username: data.username || '',
            realName: data.realName || '',
            phone: data.phone || '',
            idCard: data.idCard || '',
            building: data.building || '',
            unit: data.unit || '',
            room: data.room || '',
            avatar: formatImages(data.avatar) || '',
            userId: data.id
          }
        })
      }).catch(() => {
        // 使用本地缓存的用户信息
        this.setData({
          formData: {
            username: userInfo.username || '',
            realName: userInfo.realName || '',
            phone: userInfo.phone || '',
            idCard: userInfo.idCard || '',
            building: userInfo.building || '',
            unit: userInfo.unit || '',
            room: userInfo.room || '',
            avatar: formatImages(userInfo.avatar) || ''
          }
        })
      })
    }
  },

  onUsernameInput(e) {
    this.setData({ 'formData.username': e.detail.value })
  },

  onRealNameInput(e) {
    this.setData({ 'formData.realName': e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ 'formData.phone': e.detail.value })
  },

  onIdCardInput(e) {
    this.setData({ 'formData.idCard': e.detail.value })
  },

  onBuildingInput(e) {
    this.setData({ 'formData.building': e.detail.value })
  },

  onUnitInput(e) {
    this.setData({ 'formData.unit': e.detail.value })
  },

  onRoomInput(e) {
    this.setData({ 'formData.room': e.detail.value })
  },

  chooseAvatar() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath
        wx.showLoading({ title: '上传中...' })

        const uploadUrl = `${app.globalData.baseURL}/common/upload`

        wx.uploadFile({
          url: uploadUrl,
          filePath: tempFilePath,
          name: 'file',
          header: {
            'Authorization': `Bearer ${app.globalData.token}`
          },
          success: (uploadRes) => {
            wx.hideLoading()

            try {
              const data = JSON.parse(uploadRes.data)

              if (data.code === 200) {
                // 服务器返回的 data.url 是 /upload/xxx.jpg 格式
                const avatarUrl = data.data.url
                // 使用 formatImages 转换为完整 URL
                this.setData({ 'formData.avatar': formatImages(avatarUrl) })
              } else {
                wx.showToast({
                  title: data.message || '上传失败',
                  icon: 'none'
                })
              }
            } catch (e) {
              console.error('解析响应失败:', e)
              wx.showToast({
                title: '上传响应格式错误',
                icon: 'none'
              })
            }
          },
          fail: (err) => {
            console.error('上传失败:', err)
            wx.hideLoading()
            wx.showToast({
              title: '上传失败',
              icon: 'none'
            })
          }
        })
      }
    })
  },

  validateForm() {
    const { formData } = this.data

    if (!formData.username) {
      wx.showToast({
        title: '请输入用户名',
        icon: 'none'
      })
      return false
    }

    if (!formData.realName) {
      wx.showToast({
        title: '请输入真实姓名',
        icon: 'none'
      })
      return false
    }

    if (!formData.phone || !/^1[3-9]\d{9}$/.test(formData.phone)) {
      wx.showToast({
        title: '请输入正确的手机号',
        icon: 'none'
      })
      return false
    }

    if (!formData.idCard || !/^\d{17}[\dXx]$/.test(formData.idCard)) {
      wx.showToast({
        title: '请输入正确的身份证号',
        icon: 'none'
      })
      return false
    }

    if (!formData.building || !formData.unit || !formData.room) {
      wx.showToast({
        title: '请填写完整地址信息',
        icon: 'none'
      })
      return false
    }

    return true
  },

  saveProfile() {
    if (!this.validateForm()) return

    wx.showLoading({ title: '保存中...' })

    const { formData } = this.data
    const updateData = {
      userId: formData.userId,
      username: formData.username,
      realName: formData.realName,
      phone: formData.phone,
      idCard: formData.idCard,
      building: formData.building,
      unit: formData.unit,
      room: formData.room
    }

    // 如果有头像，保存原始路径（不包含baseURL前缀）
    if (formData.avatar) {
      const baseURL = app.globalData.baseURL || 'http://localhost:8080/api'
      let avatarPath = formData.avatar
      // 如果是完整URL，提取相对路径部分
      if (avatarPath.startsWith(baseURL)) {
        avatarPath = avatarPath.substring(baseURL.length)
      }
      updateData.avatar = avatarPath
    }

    api.updateUserInfo(updateData).then(res => {
      wx.hideLoading()

      // 更新本地用户信息
      const userInfo = app.globalData.userInfo
      app.setLoginInfo(app.globalData.token, {
        ...userInfo,
        ...updateData
      })

      wx.showToast({
        title: '保存成功',
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
