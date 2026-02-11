// pages/forum/forum.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatRelativeTime } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    categories: [],
    activeCategory: 0,
    postList: [],
    loading: false,
    isLoggedIn: false
  },

  onLoad() {
    this.loadCategories()
    this.loadPosts()
  },

  onShow() {
    // 每次显示时刷新数据
    this.loadPosts()
  },

  loadCategories() {
    api.getForumCategories().then(res => {
      this.setData({
        categories: res.data || []
      })
    }).catch(() => {
      this.setData({ categories: [] })
    })
  },

  loadPosts() {
    const isLoggedIn = app.checkLogin()
    this.setData({ isLoggedIn })

    this.setData({ loading: true })
    const { activeCategory } = this.data
    const params = { page: 1, pageSize: 20 }
    if (activeCategory !== 0) {
      params.categoryId = activeCategory
    }

    api.getForumList(params).then(res => {
      // 获取当前用户ID
      const userInfo = app.globalData.userInfo || {}
      const currentUserId = userInfo.userId || userInfo.id

      const postList = (res.data.records || []).map(item => {
        let images = []
        if (item.images) {
          try {
            images = typeof item.images === 'string' ? JSON.parse(item.images) : item.images
            // 处理图片路径
            images = formatImages(images)
          } catch (e) {
            images = []
          }
        }

        // 判断是否匿名，isPublic: 0 是匿名，1 是实名
        const isAnonymous = item.isPublic === 0
        // 判断是否为自己的帖子
        const isOwnPost = item.userId == currentUserId

        return {
          ...item,
          createTime: formatRelativeTime(item.createTime),
          images,
          userAvatar: isAnonymous ? '/images/avatar-default.png' : formatImages(item.userAvatar),
          userName: isAnonymous ? '匿名用户' : item.userName,
          isOwnPost: isOwnPost
        }
      })

      this.setData({
        postList,
        loading: false
      })

      // 检查点赞状态
      this.checkLikeStatus()
    }).catch(() => {
      this.setData({
        postList: [],
        loading: false
      })
    })
  },

  checkLikeStatus() {
    this.data.postList.forEach(post => {
      api.checkForumLike(post.id).then(res => {
        const index = this.data.postList.findIndex(item => item.id === post.id)
        if (index !== -1) {
          const postList = [...this.data.postList]
          postList[index].isLiked = res.data === true
          this.setData({ postList })
        }
      }).catch(() => {})
    })
  },

  selectCategory(e) {
    const id = e.currentTarget.dataset.id
    // 将 id 转换为数字类型
    const categoryId = parseInt(id)
    this.setData({ activeCategory: categoryId })
    this.loadPosts()
  },

  viewPostDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/forum-detail/forum-detail?id=${id}`
    })
  },

  toggleLike(e) {
    const id = e.currentTarget.dataset.id
    const liked = e.currentTarget.dataset.liked

    api.likeForumPost(id).then(() => {
      const index = this.data.postList.findIndex(item => item.id === id)
      if (index !== -1) {
        const postList = [...this.data.postList]
        postList[index].isLiked = !liked
        postList[index].likeCount = (postList[index].likeCount || 0) + (!liked ? 1 : -1)
        this.setData({ postList })
      }
    }).catch(() => {})
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url
    const urls = e.currentTarget.dataset.urls
    wx.previewImage({
      current: url,
      urls: urls
    })
  },

  // 删除帖子
  deletePost(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条帖子吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteForumPost(id).then(() => {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })
            // 刷新列表
            this.loadPosts()
          }).catch((err) => {
            console.error('删除失败:', err)
            wx.showToast({
              title: '删除失败',
              icon: 'none'
            })
          })
        }
      }
    })
  },

  // 头像加载失败处理
  onAvatarError(e) {
    console.log('头像加载失败:', e)
    // 可以在这里设置默认头像
  },

  // 阻止事件冒泡
  preventBubble() {
    // 空方法，用于阻止事件冒泡
  },

  publishPost() {
    wx.navigateTo({
      url: '/pages/forum-publish/forum-publish'
    })
  }
})
