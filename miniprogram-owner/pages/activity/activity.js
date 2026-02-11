// pages/activity/activity.js
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    activityList: [],
    loading: false
  },

  onLoad() {
    this.loadActivity()
  },

  loadActivity() {
    this.setData({ loading: true })
    api.getActivityList({ page: 1, pageSize: 20 }).then(res => {
      const records = res.data.records || []
      this.setData({
        activityList: records.map(item => ({
          id: item.id,
          title: item.title || '',
          description: item.description || '',
          coverImage: formatImages(item.coverImage),
          startTime: formatDate(item.startTime, 'YYYY-MM-DD HH:mm'),
          location: item.location || ''
        })),
        loading: false
      })
    }).catch(() => {
      this.setData({
        activityList: [],
        loading: false
      })
    })
  },

  viewDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/activity-detail/activity-detail?id=${id}`
    })
  }
})
