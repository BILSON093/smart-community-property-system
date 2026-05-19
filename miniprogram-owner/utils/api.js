// utils/api.js
const { get, post, put, del } = require('./request.js')

// ==================== 用户相关 ====================

// 用户登录
const login = (data) => post('/user/login', data)

// 业主注册
const registerOwner = (data) => post('/user/register/owner', data)

// 获取当前用户信息
const getUserInfo = () => get('/user/info')

// 更新用户信息
const updateUserInfo = (data) => put('/user/update', data)

// 修改密码
const changePassword = (data) => post('/user/change-password', data)

// ==================== 报修相关 ====================

// 获取我的报修列表
const getMyRepairs = (data) => get('/repair/my', data)

// 获取报修详情
const getRepairDetail = (id) => get(`/repair/${id}`)

// 提交报修
const addRepair = (data) => post('/repair/add', data)

// 删除报修
const deleteRepair = (id) => del(`/repair/${id}`)

// 评价维修服务
const evaluateRepair = (data) => post('/repair/evaluate', data)

// ==================== 维修员相关 ====================

// 获取维修员工单列表
const getWorkerRepairs = (data) => get('/repair/worker', data)

// 获取维修员已完成工单
const getWorkerCompletedRepairs = (data) => get('/repair/worker/completed', data)

// 开始维修
const startRepair = (repairId) => post(`/repair/start/${repairId}`)

// 完成维修
const completeRepair = (repairId) => post(`/repair/complete/${repairId}`)

// 退单
const cancelRepair = (repairId) => post(`/repair/cancel/${repairId}`)

// ==================== 费用相关 ====================

// 获取费用设置
const getFeeSettings = () => get('/fee/settings')

// 获取我的缴费列表
const getMyFees = (data) => get('/fee/my', data)

// 支付费用
const payFee = (id) => post(`/fee/pay/${id}`)

// ==================== 通知相关 ====================

// 获取通知列表
const getNoticeList = (data) => get('/notice/list', data)

// 获取通知详情
const getNoticeDetail = (id) => get(`/notice/${id}`)

// ==================== 活动相关 ====================

// 获取活动列表
const getActivityList = (data) => get('/activity/list', data)

// 获取活动详情
const getActivityDetail = (id) => get(`/activity/${id}`)

// ==================== 留言反馈相关 ====================

// 获取我的留言列表
const getMyFeedbacks = (data) => get('/feedback/my', data)

// 获取留言详情
const getFeedbackDetail = (id) => get(`/feedback/${id}`)

// 添加留言
const addFeedback = (data) => post('/feedback/add', data)

// ==================== 论坛相关 ====================

// 获取论坛列表（显示所有帖子）
const getForumList = (data) => get('/forum/list', data)

// 获取论坛帖子详情
const getForumDetail = (id) => get(`/forum/${id}`)

// 发布帖子
const addForumPost = (data) => post('/forum/add', data)

// 删除帖子
const deleteForumPost = (id) => del(`/forum/${id}`)

// 更新帖子
const updateForumPost = (data) => put('/forum/update', data)

// 获取评论列表
const getForumComments = (forumId, data) => get(`/forum/${forumId}/comments`, data)

// 添加评论
const addForumComment = (forumId, data) => post(`/forum/${forumId}/comments`, data)

// 删除评论
const deleteForumComment = (id) => del(`/forum/comments/${id}`)

// 点赞/取消点赞
const likeForumPost = (forumId) => post(`/forum/${forumId}/like`)

// 检查是否点赞
const checkForumLike = (forumId) => get(`/forum/${forumId}/like/check`)

// 获取我的帖子
const getMyPosts = () => get('/forum/my-posts')

// 获取分类列表
const getForumCategories = () => get('/forum/category/list')

// ==================== 轮播图相关 ====================

// 获取轮播图列表
const getCarouselList = () => get('/carousel/list')

// ==================== AI客服相关 ====================

// AI客服对话
const aiChat = (data) => post('/ai/chat', data)

// Agent智能助手对话
const agentChat = (data) => post('/agent/chat', data)

// 上传文件
const uploadFile = (filePath) => {
  return new Promise((resolve, reject) => {
    const app = getApp()
    wx.uploadFile({
      url: app.globalData.baseURL + '/common/upload',
      filePath: filePath,
      name: 'file',
      header: {
        'Authorization': 'Bearer ' + (app.globalData.token || wx.getStorageSync('token'))
      },
      success(res) {
        const data = JSON.parse(res.data)
        if (data.code === 200) {
          resolve(data.data)
        } else {
          reject(new Error(data.message || '上传失败'))
        }
      },
      fail(err) {
        reject(err)
      }
    })
  })
}

// 发送聊天消息（保存到数据库）
const sendChatMessage = (data) => post('/chat/send', data)

// 获取聊天历史
const getChatHistory = () => get('/chat/session')

// 获取管理员信息
const getAdminInfo = () => get('/chat/admin/info')

module.exports = {
  // 用户相关
  login,
  registerOwner,
  getUserInfo,
  updateUserInfo,
  changePassword,

  // 报修相关
  getMyRepairs,
  getRepairDetail,
  addRepair,
  deleteRepair,
  evaluateRepair,

  // 维修员相关
  getWorkerRepairs,
  getWorkerCompletedRepairs,
  startRepair,
  completeRepair,
  cancelRepair,

  // 费用相关
  getFeeSettings,
  getMyFees,
  payFee,

  // 通知相关
  getNoticeList,
  getNoticeDetail,

  // 活动相关
  getActivityList,
  getActivityDetail,

  // 留言反馈相关
  getMyFeedbacks,
  getFeedbackDetail,
  addFeedback,

  // 论坛相关
  getForumList,
  getForumDetail,
  addForumPost,
  deleteForumPost,
  updateForumPost,
  getForumComments,
  addForumComment,
  deleteForumComment,
  likeForumPost,
  checkForumLike,
  getMyPosts,
  getForumCategories,

  // 轮播图相关
  getCarouselList,

  // AI客服相关
  aiChat,
  agentChat,
  sendChatMessage,
  getChatHistory,
  getAdminInfo,
  uploadFile
}
