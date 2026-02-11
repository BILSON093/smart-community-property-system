// pages/forum-detail/forum-detail.js
const app = getApp()
const api = require('../../utils/api.js')
const { formatRelativeTime } = require('../../utils/util.js')
const { formatImages } = require('../../utils/request.js')

Page({
  data: {
    postId: null,
    post: null,
    comments: [],
    commentInput: '',
    commentImages: [],
    isAnonymousComment: false,
    loading: false
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ postId: options.id })
      this.loadPostDetail()
      this.loadComments()
    }
  },

  loadPostDetail() {
    this.setData({ loading: true })
    api.getForumDetail(this.data.postId).then(res => {
      const post = res.data

      // 获取当前用户ID，判断是否为帖子作者
      const userInfo = app.globalData.userInfo || {}
      const currentUserId = userInfo.userId || userInfo.id
      const isOwnPost = post.userId == currentUserId

      // 判断是否匿名，isPublic: 0 是匿名，1 是实名
      const isAnonymous = post.isPublic === 0

      let images = []
      if (post.images) {
        try {
          images = typeof post.images === 'string' ? JSON.parse(post.images) : post.images
          // 处理图片路径
          images = formatImages(images)
        } catch (e) {
          images = []
        }
      }

      // 检查点赞状态
      api.checkForumLike(this.data.postId).then(likeRes => {
        this.setData({
          post: {
            ...post,
            createTime: formatRelativeTime(post.createTime),
            images,
            userAvatar: isAnonymous ? '/images/avatar-default.png' : formatImages(post.userAvatar),
            userName: isAnonymous ? '匿名用户' : post.userName,
            isLiked: likeRes.data === true,
            isOwnPost: isOwnPost
          },
          loading: false
        })
      }).catch(() => {
        this.setData({
          post: {
            ...post,
            createTime: formatRelativeTime(post.createTime),
            images,
            userAvatar: isAnonymous ? '/images/avatar-default.png' : formatImages(post.userAvatar),
            userName: isAnonymous ? '匿名用户' : post.userName,
            isOwnPost: isOwnPost
          },
          loading: false
        })
      })
    }).catch(() => {
      this.setData({
        post: null,
        loading: false
      })
    })
  },

  loadComments() {
    api.getForumComments(this.data.postId, { page: 1, pageSize: 50 }).then(res => {
      // 获取当前用户ID，判断是否为评论作者
      const userInfo = app.globalData.userInfo || {}
      const currentUserId = userInfo.userId || userInfo.id

      const comments = (res.data.records || []).map(item => {
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
        // 判断是否为自己的评论
        const isOwnComment = item.userId == currentUserId
        return {
          ...item,
          createTime: formatRelativeTime(item.createTime),
          images,
          userAvatar: isAnonymous ? '/images/avatar-default.png' : formatImages(item.userAvatar),
          userName: isAnonymous ? '匿名用户' : item.userName,
          isOwnComment: isOwnComment
        }
      })

      this.setData({ comments })
    }).catch(() => {
      this.setData({ comments: [] })
    })
  },

  toggleLike() {
    if (!this.data.post) return

    api.likeForumPost(this.data.postId).then(() => {
      const post = { ...this.data.post }
      post.isLiked = !post.isLiked
      post.likeCount = (post.likeCount || 0) + (post.isLiked ? 1 : -1)
      this.setData({ post })
    }).catch(() => {})
  },

  onCommentInput(e) {
    this.setData({ commentInput: e.detail.value })
  },

  onAnonymousChange(e) {
    this.setData({ isAnonymousComment: e.detail.value })
  },

  chooseImage() {
    const count = 9 - this.data.commentImages.length
    if (count <= 0) {
      wx.showToast({
        title: '最多上传9张图片',
        icon: 'none'
      })
      return
    }

    wx.chooseMedia({
      count: count,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const tempFiles = res.tempFiles.map(file => file.tempFilePath)
        this.setData({
          commentImages: [...this.data.commentImages, ...tempFiles]
        })
      }
    })
  },

  previewCommentImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      current: this.data.commentImages[index],
      urls: this.data.commentImages
    })
  },

  deleteCommentImage(e) {
    const index = e.currentTarget.dataset.index
    const commentImages = [...this.data.commentImages]
    commentImages.splice(index, 1)
    this.setData({ commentImages })
  },

  async uploadImages() {
    const { commentImages } = this.data
    if (commentImages.length === 0) return []

    const uploadPromises = commentImages.map(filePath => {
      return new Promise((resolve, reject) => {
        wx.uploadFile({
          url: `${app.globalData.baseURL}/common/upload`,
          filePath: filePath,
          name: 'file',
          header: {
            'Authorization': `Bearer ${app.globalData.token}`
          },
          success: (res) => {
            try {
              const data = JSON.parse(res.data)
              if (data.code === 200 && data.data && data.data.url) {
                resolve(data.data.url)
              } else {
                reject(new Error(data.message || '上传失败'))
              }
            } catch (e) {
              reject(e)
            }
          },
          fail: (err) => {
            reject(err)
          }
        })
      })
    })

    return Promise.all(uploadPromises)
  },

  async submitComment() {
    const { commentInput, postId, commentImages, isAnonymousComment } = this.data
    if (!commentInput || commentInput.trim().length === 0) {
      wx.showToast({
        title: '请输入评论内容',
        icon: 'none'
      })
      return
    }

    wx.showLoading({ title: '发送中...' })

    try {
      // 上传图片
      let imageUrls = []
      if (commentImages.length > 0) {
        imageUrls = await this.uploadImages()
      }

      // 发布评论，images 需要是 JSON 字符串
      // isAnonymous: 0 是匿名，1 是实名
      await api.addForumComment(postId, {
        content: commentInput,
        images: JSON.stringify(imageUrls),
        isPublic: isAnonymousComment ? 0 : 1
      })

      wx.hideLoading()
      wx.showToast({
        title: '评论成功',
        icon: 'success'
      })
      this.setData({ commentInput: '', commentImages: [] })
      // 刷新评论列表
      this.loadComments()
      // 刷新帖子评论数
      this.loadPostDetail()
    } catch (err) {
      wx.hideLoading()
      console.error('评论失败:', err)
      wx.showToast({
        title: '评论失败',
        icon: 'none'
      })
    }
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
  deletePost() {
    if (!this.data.post) return

    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条帖子吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteForumPost(this.data.postId).then(() => {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })
            setTimeout(() => {
              wx.navigateBack()
            }, 1500)
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

  // 删除评论
  deleteComment(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条评论吗？',
      success: (res) => {
        if (res.confirm) {
          api.deleteForumComment(id).then(() => {
            wx.showToast({
              title: '删除成功',
              icon: 'success'
            })
            // 刷新评论列表
            this.loadComments()
            // 刷新帖子评论数
            this.loadPostDetail()
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
  },

  // 阻止事件冒泡
  preventBubble() {
    // 空方法
  }
})
