// utils/request.js
const app = getApp()

/**
 * 网络请求封装
 */
function request(options) {
  return new Promise((resolve, reject) => {
    const { url, method = 'GET', data = {}, header = {} } = options
    const token = app.globalData.token

    // 添加token到请求头
    if (token) {
      header.Authorization = `Bearer ${token}`
    }

    const baseURL = app.globalData.baseURL || 'http://localhost:8080/api'
    wx.request({
      url: `${baseURL}${url}`,
      method,
      data,
      timeout: 60000,
      header: {
        'Content-Type': 'application/json',
        ...header
      },
      success: (res) => {
        // 处理 401 未授权错误
        if (res.statusCode === 401) {
          app.clearLoginInfo()
          wx.hideLoading()
          wx.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          setTimeout(() => {
            wx.reLaunch({
              url: '/pages/login/login'
            })
          }, 1500)
          reject(res)
          return
        }

        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else {
            // token失效，跳转到登录页
            if (res.data.code === 401) {
              app.clearLoginInfo()
              wx.hideLoading() // 关闭 loading
              wx.showToast({
                title: '登录已过期，请重新登录',
                icon: 'none'
              })
              setTimeout(() => {
                wx.reLaunch({
                  url: '/pages/login/login'
                })
              }, 1500)
            } else {
              wx.hideLoading() // 关闭 loading
              wx.showToast({
                title: res.data.message || '请求失败',
                icon: 'none'
              })
            }
            reject(res.data)
          }
        } else {
          wx.hideLoading() // 关闭 loading
          wx.showToast({
            title: '网络请求失败',
            icon: 'none'
          })
          reject(res)
        }
      },
      fail: (err) => {
        wx.hideLoading() // 关闭 loading
        wx.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// GET请求
function get(url, data = {}) {
  return request({
    url,
    method: 'GET',
    data
  })
}

// POST请求
function post(url, data = {}) {
  return request({
    url,
    method: 'POST',
    data
  })
}

// PUT请求
function put(url, data = {}) {
  return request({
    url,
    method: 'PUT',
    data
  })
}

// DELETE请求
function del(url, data = {}) {
  return request({
    url,
    method: 'DELETE',
    data
  })
}

/**
 * 处理图片路径，将相对路径转为绝对路径
 * @param {String|Array} images - 图片路径或图片数组
 * @returns {String|Array} 处理后的图片路径
 */
function formatImages(images) {
  const baseURL = getApp().globalData.baseURL

  if (!images) return images

  // 如果是数组，处理每个图片
  if (Array.isArray(images)) {
    return images.map(img => formatSingleImage(img, baseURL))
  }

  // 如果是字符串，处理单个图片
  return formatSingleImage(images, baseURL)
}

/**
 * 处理单个图片路径
 */
function formatSingleImage(img, baseURL) {
  if (!img) return img

  // 如果已经是完整的URL（http/https），直接返回
  if (typeof img === 'string' && (img.startsWith('http://') || img.startsWith('https://'))) {
    return img
  }

  // 如果是 /upload/ 开头的相对路径，拼接 baseURL
  if (typeof img === 'string' && img.startsWith('/upload/')) {
    return `${baseURL}${img}`
  }

  return img
}

module.exports = {
  request,
  get,
  post,
  put,
  del,
  formatImages
}
