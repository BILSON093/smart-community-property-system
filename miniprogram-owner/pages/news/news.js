// pages/news/news.js
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    noticeList: [],
    loading: false
  },

  onLoad(options) {
    this.loadNoticeList()
  },

  // 加载通知列表
  loadNoticeList() {
    this.setData({ loading: true })
    api.getNoticeList({ page: 1, pageSize: 20 }).then(res => {
      this.setData({
        noticeList: (res.data.records || []).map(item => ({
          ...item,
          publishTime: formatDate(item.publishTime, 'YYYY-MM-DD HH:mm')
        })),
        loading: false
      })
    }).catch(() => {
      this.setData({
        noticeList: [],
        loading: false
      })
    })
  },

  // 查看详情 - 跳转到详情页
  viewDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/news-detail/news-detail?id=${id}`
    })
  }
})
