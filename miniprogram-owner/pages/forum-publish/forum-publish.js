// pages/forum-publish/forum-publish.js
const app = getApp()
const api = require('../../utils/api.js')

Page({
  data: {
    categories: [],
    selectedCategoryId: null,
    selectedCategoryName: '',
    title: '',
    content: '',
    imageList: [],
    maxImages: 9,
    isAnonymous: false
  },

  onLoad() {
    this.loadCategories()
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

  onTitleInput(e) {
    this.setData({ title: e.detail.value })
  },

  onCategoryChange(e) {
    const index = parseInt(e.detail.value)
    const category = this.data.categories[index]
    if (category) {
      this.setData({
        selectedCategoryId: category.id,
        selectedCategoryName: category.name
      })
    }
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  onAnonymousChange(e) {
    this.setData({ isAnonymous: e.detail.value })
  },

  chooseImage() {
    const count = this.data.maxImages - this.data.imageList.length
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
          imageList: [...this.data.imageList, ...tempFiles]
        })
      }
    })
  },

  deleteImage(e) {
    const index = e.currentTarget.dataset.index
    const imageList = [...this.data.imageList]
    imageList.splice(index, 1)
    this.setData({ imageList })
  },

  previewImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({
      current: this.data.imageList[index],
      urls: this.data.imageList
    })
  },

  validateForm() {
    const { title, content, selectedCategoryId } = this.data

    if (!title || title.trim().length === 0) {
      wx.showToast({
        title: '请输入标题',
        icon: 'none'
      })
      return false
    }

    if (!content || content.trim().length === 0) {
      wx.showToast({
        title: '请输入内容',
        icon: 'none'
      })
      return false
    }

    if (!selectedCategoryId) {
      wx.showToast({
        title: '请选择分类',
        icon: 'none'
      })
      return false
    }

    return true
  },

  async uploadImages() {
    const { imageList } = this.data
    if (imageList.length === 0) return []

    const uploadPromises = imageList.map(filePath => {
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

  async submitPost() {
    if (!this.validateForm()) return

    wx.showLoading({ title: '发布中...' })

    try {
      // 上传图片
      let imageUrls = []
      if (this.data.imageList.length > 0) {
        imageUrls = await this.uploadImages()
      }

      // 发布帖子，images 需要是 JSON 字符串
      // isPublic: 0 是匿名，1 是实名
      await api.addForumPost({
        title: this.data.title,
        content: this.data.content,
        categoryId: this.data.selectedCategoryId,
        images: JSON.stringify(imageUrls),
        isPublic: this.data.isAnonymous ? 0 : 1
      })

      wx.hideLoading()
      wx.showToast({
        title: '发布成功',
        icon: 'success'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (err) {
      wx.hideLoading()
      console.error('发布失败:', err)
      wx.showToast({
        title: err?.message || '发布失败',
        icon: 'none'
      })
    }
  }
})
