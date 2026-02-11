// pages/repair-detail/repair-detail.js
const api = require('../../utils/api.js')
const { formatDate, formatRelativeTime } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

const STATUS_MAP = {
  0: { text: '待处理', tip: '您的报修已提交，等待处理' },
  1: { text: '已派单', tip: '维修员已分配，等待维修' },
  2: { text: '维修中', tip: '维修员正在处理您的报修' },
  3: { text: '已完成', tip: '报修已完成，请评价' },
  4: { text: '已取消', tip: '报修已取消' }
}

Page({
  data: {
    repair: null,
    loading: false,
    showEvaluate: false,
    evaluateScore: 0,
    evaluateComment: ''
  },

  onLoad(options) {
    if (options.id) {
      this.loadRepairDetail(options.id)
    }
  },

  loadRepairDetail(id) {
    this.setData({ loading: true })
    api.getRepairDetail(id).then(res => {
      const repair = res.data
      let images = []
      if (repair.images) {
        try {
          images = typeof repair.images === 'string' ? JSON.parse(repair.images) : repair.images
          // 处理图片路径
          images = formatImages(images)
        } catch (e) {
          images = []
        }
      }

      // 生成评分星星
      let ratingStars = ''
      if (repair.evaluation && repair.evaluation.score) {
        ratingStars = '⭐'.repeat(repair.evaluation.score)
      }

      this.setData({
        repair: {
          ...repair,
          statusText: STATUS_MAP[repair.status]?.text || '未知',
          statusTip: STATUS_MAP[repair.status]?.tip || '',
          createTime: formatDate(repair.createTime, 'YYYY-MM-DD HH:mm'),
          startTime: repair.startTime ? formatDate(repair.startTime, 'YYYY-MM-DD HH:mm') : '',
          completeTime: repair.completeTime ? formatDate(repair.completeTime, 'YYYY-MM-DD HH:mm') : '',
          images,
          ratingStars
        },
        loading: false
      })
    }).catch(() => {
      this.setData({
        repair: null,
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
  },

  // 显示评价弹窗
  showEvaluateModal() {
    console.log('打开评价弹窗，重置分数为0')
    this.setData({
      showEvaluate: true,
      evaluateScore: 0,
      evaluateComment: ''
    })
    console.log('打开后的 evaluateScore:', this.data.evaluateScore)
  },

  // 关闭评价弹窗
  closeEvaluate() {
    this.setData({
      showEvaluate: false
    })
  },

  // 选择评分
  selectScore(e) {
    const score = e.currentTarget.dataset.score
    console.log('点击评分:', score)
    this.setData({
      evaluateScore: score
    })
    console.log('设置后的 evaluateScore:', this.data.evaluateScore)
  },

  // 阻止事件冒泡（用于模态框内容区域）
  preventBubble() {
    // 空方法，仅用于阻止点击事件冒泡
  },

  // 输入评价内容
  onCommentInput(e) {
    this.setData({
      evaluateComment: e.detail.value
    })
  },

  // 提交评价
  submitEvaluate() {
    const { evaluateScore, evaluateComment } = this.data

    // 验证评分
    if (evaluateScore === 0) {
      wx.showToast({
        title: '请选择评分',
        icon: 'none'
      })
      return
    }

    // 验证评价内容
    if (!evaluateComment || evaluateComment.trim().length === 0) {
      wx.showToast({
        title: '请输入评价内容',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '提交中...' })
    api.evaluateRepair({
      repairId: this.data.repair.id,
      score: evaluateScore,
      comment: evaluateComment.trim()
    }).then(() => {
      try {
        wx.hideLoading()
      } catch (e) {
        // 忽略 hideLoading 错误
      }
      wx.showToast({
        title: '评价成功',
        icon: 'success'
      })
      // 关闭弹窗并刷新页面
      this.setData({
        showEvaluate: false
      })
      this.loadRepairDetail(this.data.repair.id)
    }).catch(() => {
      // request.js 中已经处理了错误和 loading
    })
  }
})
