// pages/activity-detail/activity-detail.js
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    activity: null,
    activityImages: [],
    loading: false
  },

  onLoad(options) {
    if (options.id) {
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
          // 处理图片路径
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
    }).catch(() => {
      this.setData({
        activity: null,
        loading: false
      })
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
