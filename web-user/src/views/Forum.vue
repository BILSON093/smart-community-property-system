<template>
  <div class="forum-container">
    <div class="forum-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          <van-icon name="arrow-left" style="font-size: 18px;" />
          返回
        </button>
        <h2>社区论坛</h2>
      </div>
      <div class="header-right">
        <van-dropdown-menu style="width: 130px; margin-right: 10px">
          <van-dropdown-item v-model="selectedCategory" :options="categoryOptions" @change="loadForums" />
        </van-dropdown-menu>
        <van-button type="primary" size="small" icon="plus" @click="showAddDialog = true">发布帖子</van-button>
      </div>
    </div>

    <div class="forum-content">
      <div class="post-list">
        <div v-for="item in list" :key="item.id" class="post-card" :class="{ pinned: item.isPinned }">
          <div v-if="item.isPinned" class="pinned-badge">
            <van-icon name="star" />
            置顶
          </div>
          <div class="post-header">
            <div class="user-avatar">
              <div class="avatar-circle" v-if="item.isPublic === 1 && item.userAvatar">
                <img :src="item.userAvatar" alt="头像" />
              </div>
              <div class="avatar-circle" v-else>
                <span>{{ item.isPublic === 1 ? (item.userName || '用户').charAt(0) : '匿' }}</span>
              </div>
            </div>
            <div class="user-info">
              <span class="username">{{ item.isPublic === 1 ? item.userName : '匿名用户' }}</span>
              <span class="time">{{ formatTime(item.createTime) }}</span>
              <van-tag v-if="item.categoryName" type="primary" style="margin-left: 8px">{{ item.categoryName }}</van-tag>
            </div>
          </div>
          <div v-if="item.title" class="post-title">{{ item.title }}</div>
          <div class="post-content">{{ item.content }}</div>
          <div v-if="item.images" class="post-images">
            <van-image
              v-for="(img, index) in getImages(item.images)"
              :key="index"
              :src="img"
              fit="cover"
              width="100"
              height="100"
              radius="8"
              @click="previewImage(getImages(item.images), index)"
              style="cursor: pointer"
            />
          </div>
          <div class="post-actions">
            <van-button @click="toggleLike(item)" :type="item.isLiked ? 'warning' : 'default'" size="small" plain>
              <van-icon :name="item.isLiked ? 'star' : 'star-o'" style="margin-right: 4px" />
              {{ item.likeCount || 0 }}
            </van-button>
            <van-button @click="handleViewComments(item)" size="small" plain>
              <van-icon name="chat-o" style="margin-right: 4px" />
              评论({{ item.commentCount || 0 }})
            </van-button>
            <van-button v-if="item.userId === currentUserId" @click="handleDeletePost(item)" type="danger" size="small" plain>
              删除
            </van-button>
          </div>
        </div>
      </div>
    </div>

    <van-popup v-model:show="showAddDialog" position="bottom" round :style="{ padding: '24px', maxHeight: '85vh' }">
      <div class="popup-title">发布帖子</div>
      <van-cell-group inset>
        <van-field v-model="formData.title" label="标题" placeholder="请输入标题" />
        <van-field v-model="formData.categoryId" label="分类" placeholder="请选择分类" readonly is-link @click="showCategoryPicker = true" :value="getCategoryName(formData.categoryId)" />
        <van-field v-model="formData.content" label="内容" type="textarea" rows="4" placeholder="请输入内容" />
      </van-cell-group>
      <div class="upload-section">
        <div class="upload-label">图片</div>
        <van-uploader v-model="imageFileList" :after-read="afterImageRead" :before-read="beforeImageUpload" multiple :max-count="9" />
      </div>
      <div class="switch-section">
        <span class="switch-label">是否匿名</span>
        <van-switch v-model="formData.isPublic" :active-value="1" :inactive-value="0" size="20" />
        <span style="margin-left: 10px; color: #909399">{{ formData.isPublic === 1 ? '实名' : '匿名' }}</span>
      </div>
      <div class="popup-actions">
        <van-button @click="showAddDialog = false" style="margin-right: 12px">取消</van-button>
        <van-button type="primary" @click="handleAdd" :loading="loading">发布</van-button>
      </div>
    </van-popup>

    <van-popup v-model:show="showCategoryPicker" position="bottom" round>
      <van-picker
        :columns="categoryPickerColumns"
        @confirm="onCategoryConfirm"
        @cancel="showCategoryPicker = false"
        title="选择分类"
      />
    </van-popup>

    <van-popup v-model:show="showCommentDialog" position="bottom" round :style="{ padding: '24px', maxHeight: '85vh' }">
      <div class="popup-title">评论列表</div>
      <div style="max-height: 400px; overflow-y: auto; margin-bottom: 20px">
        <van-empty v-if="commentList.length === 0" description="暂无评论" />
        <div v-for="comment in commentList" :key="comment.id" class="comment-item">
          <div class="avatar-circle avatar-sm" v-if="comment.isPublic === 1 && comment.userAvatar">
            <img :src="comment.userAvatar" alt="头像" />
          </div>
          <div class="avatar-circle avatar-sm" v-else>
            <span>{{ comment.isPublic === 1 ? (comment.userName || '用户').charAt(0) : '匿' }}</span>
          </div>
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-username">{{ comment.isPublic === 1 ? comment.userName : '匿名用户' }}</span>
              <span class="comment-time">{{ comment.createTime }}</span>
              <van-button v-if="comment.userId === currentUserId" @click="handleDeleteComment(comment)" type="danger" size="mini" plain>
                删除
              </van-button>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div v-if="comment.images" class="comment-images">
              <van-image
                v-for="(img, index) in getImages(comment.images)"
                :key="index"
                :src="img"
                fit="cover"
                width="60"
                height="60"
                radius="4"
                style="margin-right: 8px"
                @click="previewImage(getImages(comment.images), index)"
              />
            </div>
          </div>
        </div>
      </div>
      <div style="border-top: 1px solid #e4e7ed; padding-top: 20px">
        <van-cell-group inset>
          <van-field v-model="commentForm.content" label="内容" type="textarea" rows="3" placeholder="输入评论内容..." />
        </van-cell-group>
        <div class="upload-section">
          <div class="upload-label">图片</div>
          <van-uploader v-model="commentImageFileList" :after-read="afterCommentImageRead" :before-read="beforeImageUpload" multiple :max-count="9" />
        </div>
        <div class="switch-section">
          <span class="switch-label">是否匿名</span>
          <van-switch v-model="commentForm.isAnonymous" :active-value="1" :inactive-value="0" size="20" />
          <span style="margin-left: 10px; color: #909399">{{ commentForm.isAnonymous === 1 ? '匿名' : '实名' }}</span>
        </div>
      </div>
      <div class="popup-actions">
        <van-button @click="showCommentDialog = false" style="margin-right: 12px">关闭</van-button>
        <van-button type="primary" @click="handleAddComment" :loading="loading">发表评论</van-button>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { showToast, showDialog, showImagePreview } from 'vant'
import request from '@/utils/request'
import router from '@/router'

const list = ref([])
const categoryList = ref([])
const selectedCategory = ref(null)
const loading = ref(false)
const showAddDialog = ref(false)
const showCommentDialog = ref(false)
const imageList = ref([])
const imageFileList = ref([])
const commentList = ref([])
const commentImageList = ref([])
const commentImageFileList = ref([])
const currentForumId = ref(null)
const currentUserId = ref(null)
const showCategoryPicker = ref(false)

// Get current user ID from localStorage
const getCurrentUserId = () => {
  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    try {
      const parsed = JSON.parse(userInfo)
      currentUserId.value = parsed.userId
    } catch (e) {
      console.error('Failed to parse userInfo:', e)
    }
  }
}

onMounted(() => {
  getCurrentUserId()
  loadForums()
  loadCategories()
})

const formData = ref({
  categoryId: null,
  title: '',
  content: '',
  images: '',
  isPublic: 1
})
const commentForm = ref({
  content: '',
  images: '',
  isAnonymous: 0
})

const goBack = () => {
  router.back()
}

const loadForums = async () => {
  try {
    loading.value = true
    const res = await request.get('/forum/list', { 
      params: { page: 1, size: 100, categoryId: selectedCategory.value } 
    })
    const forums = res.data.records || []
    
    if (currentUserId.value) {
      for (const forum of forums) {
        try {
          const likeRes = await request.get(`/forum/${forum.id}/like/check`)
          forum.isLiked = likeRes.data || false
        } catch (error) {
          forum.isLiked = false
        }
      }
    }
    
    list.value = forums
  } catch (error) {
    console.error('加载失败:', error)
    showToast('加载失败')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await request.get('/forum/category/list')
    categoryList.value = (res.data || []).filter(c => c.id != null)
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const categoryOptions = [
  { text: '全部分类', value: null },
  ...categoryList.value.map(c => ({ text: c.name, value: c.id }))
]

const categoryPickerColumns = categoryList.value.map(c => ({ text: c.name, value: c.id }))

const getCategoryName = (id) => {
  if (!id) return ''
  const cat = categoryList.value.find(c => c.id === id)
  return cat ? cat.name : ''
}

const onCategoryConfirm = ({ selectedValues }) => {
  formData.value.categoryId = selectedValues[0] || null
  showCategoryPicker.value = false
}

const previewImage = (images, index) => {
  showImagePreview({ images, startPosition: index })
}

const resetFormData = () => {
  formData.value = {
    categoryId: null,
    title: '',
    content: '',
    images: '',
    isPublic: 1
  }
  imageList.value = []
  imageFileList.value = []
}

const resetCommentForm = () => {
  commentForm.value = {
    content: '',
    images: '',
    isAnonymous: 0
  }
  commentImageList.value = []
  commentImageFileList.value = []
}

const beforeImageUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt5M = rawFile.size / 1024 / 1024 < 20
  if (!isImage) {
    showToast('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    showToast('图片大小不能超过 20MB!')
    return false
  }
  return true
}

const uploadFile = async (file) => {
  const formDataObj = new FormData()
  formDataObj.append('file', file)
  const res = await request.post('/common/upload', formDataObj, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
  return res.data.url
}

const afterImageRead = async (file) => {
  try {
    file.status = 'uploading'
    file.message = '上传中...'
    const url = await uploadFile(file.file)
    file.url = url
    file.status = 'done'
    file.message = ''
    imageList.value.push({ url, status: 'success' })
  } catch (error) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
  }
}

const afterCommentImageRead = async (file) => {
  try {
    file.status = 'uploading'
    file.message = '上传中...'
    const url = await uploadFile(file.file)
    file.url = url
    file.status = 'done'
    file.message = ''
    commentImageList.value.push({ url, status: 'success' })
  } catch (error) {
    file.status = 'failed'
    file.message = '上传失败'
    showToast('图片上传失败')
  }
}

const handleAdd = async () => {
  if (!formData.value.content) {
    showToast('请填写必要信息')
    return
  }

  const imagesArray = imageList.value
    .filter(item => (item.raw?.url || item.url) && item.status === 'success')
    .map(item => item.raw?.url || item.url)

  console.log('imageList:', JSON.stringify(imageList.value))
  console.log('imagesArray:', JSON.stringify(imagesArray))

  if (imagesArray.length > 0 && imagesArray.length !== imageList.value.length) {
    showToast('请等待图片上传完成')
    return
  }

  loading.value = true

  try {
    const postData = {
      categoryId: formData.value.categoryId,
      title: formData.value.title,
      content: formData.value.content,
      images: imagesArray.length > 0 ? JSON.stringify(imagesArray) : '[]',
      isPublic: formData.value.isPublic
    }
    console.log(' posting data:', JSON.stringify(postData))
    await request.post('/forum/add', postData)

    showToast('发布成功，帖子已提交审核')
    showAddDialog.value = false
    resetFormData()
    loadForums()
  } catch (error) {
    showToast('发布失败')
  } finally {
    loading.value = false
  }
}

const handleViewComments = async (row) => {
  try {
    currentForumId.value = row.id
    const res = await request.get(`/forum/${row.id}/comments`, { params: { page: 1, size: 100 } })
    commentList.value = res.data.records || []
    showCommentDialog.value = true
    resetCommentForm()
  } catch (error) {
    showToast('加载评论失败')
  }
}

const handleAddComment = async () => {
  if (!commentForm.value.content.trim() && commentImageList.value.length === 0) {
    showToast('请输入评论内容或上传图片')
    return
  }

  const imagesArray = commentImageList.value
    .filter(item => (item.raw?.url || item.url) && item.status === 'success')
    .map(item => item.raw?.url || item.url)

  if (imagesArray.length > 0 && imagesArray.length !== commentImageList.value.length) {
    showToast('请等待图片上传完成')
    return
  }

  loading.value = true

  try {
    await request.post(`/forum/${currentForumId.value}/comments`, {
      content: commentForm.value.content,
      images: imagesArray.length > 0 ? JSON.stringify(imagesArray) : '[]',
      isPublic: commentForm.value.isAnonymous ? 0 : 1
    })
    showToast('评论成功')
    resetCommentForm()
    await loadComments(currentForumId.value)
    loadForums()
  } catch (error) {
    showToast('评论失败')
  } finally {
    loading.value = false
  }
}

const loadComments = async (forumId) => {
  try {
    const res = await request.get(`/forum/${forumId}/comments`, { params: { page: 1, size: 100 } })
    commentList.value = res.data.records || []
  } catch (error) {
    showToast('加载评论失败')
  }
}

const getImages = (images) => {
  if (!images) return []
  return JSON.parse(images)
}

const toggleLike = async (item) => {
  try {
    await request.post(`/forum/${item.id}/like`)
    item.isLiked = !item.isLiked
    item.likeCount = item.isLiked ? (item.likeCount || 0) + 1 : Math.max((item.likeCount || 0) - 1, 0)
  } catch (error) {
    console.error('操作失败:', error)
    showToast('操作失败')
  }
}

const handleDeletePost = async (item) => {
  try {
    await request.delete(`/forum/${item.id}`)
    showToast('删除成功')
    loadForums()
  } catch (error) {
    console.error('删除失败:', error)
    showToast('删除失败')
  }
}

const handleDeleteComment = async (comment) => {
  try {
    await request.delete(`/forum/comments/${comment.id}`)
    showToast('删除成功')
    await loadComments(currentForumId.value)
  } catch (error) {
    console.error('删除失败:', error)
    showToast('删除失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  return time.split(' ')[0]
}
</script>

<style scoped>
.forum-container {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-right {
  display: flex;
  align-items: center;
}

.forum-header h2 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.forum-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.post-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  position: relative;
}

.post-card.pinned {
  border: 2px solid #f56c6c;
  box-shadow: 0 4px 16px rgba(245, 108, 108, 0.2);
}

.pinned-badge {
  position: absolute;
  top: -12px;
  right: 12px;
  background: linear-gradient(135deg, #f56c6c 0%, #ff8c00 100%);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
  box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
}

.post-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-4px);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.time {
  font-size: 13px;
  color: #909399;
}

.post-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.post-content {
  font-size: 15px;
  line-height: 1.6;
  color: #606266;
  margin-bottom: 16px;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
  margin-bottom: 16px;
}

.post-actions {
  display: flex;
  gap: 20px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.image-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.image-uploader:hover {
  border-color: var(--el-color-primary);
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.comment-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-username {
  font-weight: 600;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-text {
  color: #606266;
  line-height: 1.6;
}

.comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.btn-back {
  background: #f0f9ff;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #e6f7ff;
  transform: translateY(-2px);
}
</style>
