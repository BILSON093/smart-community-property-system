// pages/activity-detail/activity-detail.js
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    activity: null,
    activityImages: [],
    loading: false,
    signedUp: false,
    signupCount: 0,
    signupLoading: false
  },

  onLoad(options) {
    if (options.id) {
      this.activityId = options.id
      this.loadActivityDetail(options.id)
    }
  },

  loadActivityDetail(id) {
    this.setData({ loading: true })
    api.getActivityDetail(id).then(res => {
      const activity = res.data || {}
      let activityImages = []
      if (activity.images) {
        try {
          activityImages = typeof activity.images === 'string'
            ? JSON.parse(activity.images)
            : activity.images
          activityImages = formatImages(activityImages)
        } catch (e) {
          activityImages = []
        }
      }

      this.setData({
        activity: {
          id: activity.id,
          title: activity.title || '',
          description: activity.description || '',
          coverImage: formatImages(activity.coverImage),
          startTime: formatDate(activity.startTime, 'YYYY-MM-DD HH:mm'),
          endTime: formatDate(activity.endTime, 'YYYY-MM-DD HH:mm'),
          location: activity.location || ''
        },
        activityImages,
        loading: false
      })

      this.loadSignupStatus()
    }).catch(() => {
      this.setData({
        activity: null,
        loading: false
      })
    })
  },

  loadSignupStatus() {
    if (!this.activityId) return
    api.getSignupStatus(this.activityId).then(res => {
      if (res.code === 200 && res.data) {
        this.setData({
          signedUp: res.data.signedUp || false,
          signupCount: res.data.count || 0
        })
      }
    }).catch(() => {})
  },

  handleSignup() {
    this.setData({ signupLoading: true })
    api.signupActivity(this.activityId).then(res => {
      if (res.code === 200) {
        wx.showToast({ title: '报名成功', icon: 'success' })
        this.loadSignupStatus()
      } else {
        wx.showToast({ title: res.message || '报名失败', icon: 'none' })
      }
    }).catch(() => {
      wx.showToast({ title: '报名失败', icon: 'none' })
    }).finally(() => {
      this.setData({ signupLoading: false })
    })
  },

  handleCancelSignup() {
    wx.showModal({
      title: '提示',
      content: '确定取消报名吗？',
      success: (res) => {
        if (res.confirm) {
          this.setData({ signupLoading: true })
          api.cancelSignupActivity(this.activityId).then(res => {
            if (res.code === 200) {
              wx.showToast({ title: '取消成功', icon: 'success' })
              this.loadSignupStatus()
            } else {
              wx.showToast({ title: res.message || '取消失败', icon: 'none' })
            }
          }).catch(() => {
            wx.showToast({ title: '取消失败', icon: 'none' })
          }).finally(() => {
            this.setData({ signupLoading: false })
          })
        }
      }
    })
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url
    const urls = e.currentTarget.dataset.urls
    wx.previewImage({
      current: url,
      urls: urls
    })
  }
})
