// pages/pay/pay.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatDate } = require('../../utils/util.js')

Page({
  data: {
    feeList: [],
    filteredFees: [],
    activeFilter: 'all',
    totalUnpaid: 0,
    totalPaid: 0,
    loading: false,
    isLoggedIn: false,
    showPayDialog: false,
    currentFee: null,
    selectedMethod: 'alipay',
    feeSettings: [] // 费用设置
  },

  onLoad() {
    this.loadFeeSettings()
    this.loadFees()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadFees()
  },

  // 加载费用设置
  loadFeeSettings() {
    api.getFeeSettings().then(res => {
      const data = res.data || {}
      // 转换驼峰命名为小写命名
      const settings = {
        waterFee: data.water_fee || data.waterFee,
        electricityFee: data.electricity_fee || data.electricityFee,
        gasFee: data.gas_fee || data.gasFee,
        propertyFee: data.property_fee || data.propertyFee
      }
      this.setData({ feeSettings: settings })
    }).catch(err => {
      console.error('加载费用设置失败:', err)
    })
  },

  // 根据费用类型获取单价和单位
  getFeeSetting(type) {
    const { feeSettings } = this.data
    const typeMap = {
      '水费': { unit_price: feeSettings.waterFee, unit_name: '吨' },
      '电费': { unit_price: feeSettings.electricityFee, unit_name: '度' },
      '燃气费': { unit_price: feeSettings.gasFee, unit_name: '立方米' }
    }
    return typeMap[type] || {}
  },

  loadFees() {
    const isLoggedIn = app.checkLogin()
    this.setData({ isLoggedIn })

    if (!isLoggedIn) {
      this.setData({
        feeList: [],
        filteredFees: [],
        totalUnpaid: 0,
        totalPaid: 0,
        loading: false
      })
      return
    }

    this.setData({ loading: true })
    api.getMyFees({ page: 1, pageSize: 50 }).then(res => {
      const { feeSettings } = this.data

      // 处理费用数据,计算用量
      const feeList = (res.data.records || []).map(item => {
        const setting = this.getFeeSetting(item.type)
        const usage = setting.unit_price ? (parseFloat(item.amount) / setting.unit_price).toFixed(2) : null

        return {
          ...item,
          payTime: item.payTime ? formatDate(item.payTime, 'YYYY-MM-DD HH:mm') : '',
          usage: usage,
          unit: setting.unit_name || ''
        }
      })

      // 计算总额
      const totalUnpaid = feeList
        .filter(item => item.status === 0)
        .reduce((sum, item) => sum + parseFloat(item.amount), 0)
        .toFixed(2)

      const totalPaid = feeList
        .filter(item => item.status === 1)
        .reduce((sum, item) => sum + parseFloat(item.amount), 0)
        .toFixed(2)

      this.setData({
        feeList,
        filteredFees: feeList,
        totalUnpaid,
        totalPaid,
        loading: false
      })
    }).catch(() => {
      this.setData({
        feeList: [],
        filteredFees: [],
        totalUnpaid: '0.00',
        totalPaid: '0.00',
        loading: false
      })
    })
  },

  switchFilter(e) {
    const filter = e.currentTarget.dataset.filter
    this.setData({ activeFilter: filter })
    this.filterFees()
  },

  filterFees() {
    const { feeList, activeFilter } = this.data
    let filtered = feeList

    switch (activeFilter) {
      case 'unpaid':
        filtered = feeList.filter(item => item.status === 0)
        break
      case 'paid':
        filtered = feeList.filter(item => item.status === 1)
        break
      case 'all':
      default:
        filtered = feeList
        break
    }

    this.setData({ filteredFees: filtered })
  },

  // 显示支付弹窗
  showPayDialog(e) {
    const id = e.currentTarget.dataset.id
    const fee = this.data.feeList.find(item => item.id === id)
    this.setData({
      showPayDialog: true,
      currentFee: fee,
      selectedMethod: 'alipay'
    })
  },

  // 关闭支付弹窗
  closePayDialog() {
    this.setData({
      showPayDialog: false,
      currentFee: null,
      selectedMethod: 'alipay'
    })
  },

  // 选择支付方式
  selectMethod(e) {
    const method = e.currentTarget.dataset.method
    this.setData({
      selectedMethod: method
    })
  },

  // 阻止事件冒泡
  preventBubble() {
    // 空方法，仅用于阻止点击事件冒泡
  },

  // 确认支付
  confirmPay() {
    const { currentFee } = this.data
    if (!currentFee) return

    wx.showLoading({ title: '支付中...' })
    api.payFee(currentFee.id).then(() => {
      wx.hideLoading()
      wx.showToast({
        title: '支付成功',
        icon: 'success'
      })
      // 关闭弹窗并刷新列表
      this.closePayDialog()
      this.loadFees()
    }).catch(() => {
      wx.hideLoading()
    })
  }
})
