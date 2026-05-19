<template>
  <div class="forum-container">
    <div class="forum-header">
      <div class="header-left">
        <h2>社区论坛</h2>
      </div>
      <div class="header-right">
        <div class="category-tabs">
          <span
            v-for="cat in allCategories"
            :key="cat.value"
            :class="['category-tab', { active: selectedCategory === cat.value }]"
            @click="selectedCategory = cat.value; loadForums()"
          >{{ cat.text }}</span>
        </div>
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

    <van-popup v-model:show="showAddDialog" round :style="{ padding: '24px', width: '560px', maxWidth: '90vw', maxHeight: '85vh' }">
      <div class="popup-title">发布帖子</div>
      <div class="form-group">
        <label class="form-label">标题</label>
        <input v-model="formData.title" class="form-input" placeholder="请输入标题" />
      </div>
      <div class="form-group">
        <label class="form-label">分类</label>
        <div class="category-select">
          <span
            v-for="cat in categoryList"
            :key="cat.id"
            :class="['cat-option', { active: formData.categoryId === cat.id }]"
            @click="formData.categoryId = cat.id"
          >{{ cat.name }}</span>
        </div>
      </div>
      <div class="form-group">
        <label class="form-label">内容</label>
        <textarea v-model="formData.content" class="form-textarea" rows="5" placeholder="请输入内容"></textarea>
      </div>
      <div class="form-group">
        <label class="form-label">图片</label>
        <van-uploader v-model="imageFileList" :after-read="afterImageRead" :before-read="beforeImageUpload" multiple :max-count="9" />
      </div>
      <div class="form-group form-row">
        <span class="form-label" style="margin-bottom: 0">匿名发布</span>
        <van-switch v-model="formData.isPublic" :active-value="1" :inactive-value="0" size="20" />
        <span class="form-hint">{{ formData.isPublic === 1 ? '实名' : '匿名' }}</span>
      </div>
      <div class="popup-actions">
        <van-button @click="showAddDialog = false" style="margin-right: 12px">取消</van-button>
        <van-button type="primary" @click="handleAdd" :loading="loading">发布</van-button>
      </div>
    </van-popup>

    <van-popup v-model:show="showCommentDialog" round :style="{ padding: '24px', width: '560px', maxWidth: '90vw', maxHeight: '85vh' }">
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
import { ref, computed, onMounted } from 'vue'
import { showToast, showImagePreview } from 'vant'
import request from '@/utils/request'

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

const allCategories = computed(() => [
  { text: '全部', value: null },
  ...categoryList.value.map(c => ({ text: c.name, value: c.id }))
])



const previewImage = (images, index) => {
  showImagePreview({ images, startPosition: index, closeable: true })
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
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.forum-header h2 {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.category-tabs {
  display: flex;
  gap: 4px;
  background: var(--bg-page);
  border-radius: var(--radius-sm);
  padding: 4px;
}

.category-tab {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.category-tab:hover {
  color: var(--primary);
  background: rgba(79, 110, 247, 0.06);
}

.category-tab.active {
  background: var(--primary);
  color: #fff;
  font-weight: 500;
}

.forum-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.post-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: 20px 24px;
  box-shadow: var(--shadow-sm);
  transition: all 0.2s ease;
  position: relative;
}

.post-card.pinned {
  border-left: 3px solid var(--danger);
}

.pinned-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(239, 68, 68, 0.1);
  color: var(--danger);
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.post-card:hover {
  box-shadow: var(--shadow-hover);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.user-avatar {
  flex-shrink: 0;
}

.avatar-circle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: var(--primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.avatar-circle img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-sm {
  width: 32px;
  height: 32px;
  font-size: 13px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}

.time {
  font-size: 12px;
  color: var(--text-muted);
}

.post-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.post-content {
  font-size: 14px;
  line-height: 1.7;
  color: var(--text-secondary);
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-images {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.post-actions {
  display: flex;
  gap: 12px;
  padding-top: 12px;
  border-top: 1px solid #F1F5F9;
}

.comment-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  padding: 14px;
  background: #FAFBFD;
  border-radius: var(--radius-sm);
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
  color: var(--text-primary);
  font-size: 14px;
}

.comment-time {
  font-size: 12px;
  color: var(--text-muted);
}

.comment-text {
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: 14px;
}

.comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.popup-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 24px;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-primary);
  outline: none;
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-input:focus {
  border-color: var(--primary);
}

.form-textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-sm);
  font-size: 14px;
  color: var(--text-primary);
  outline: none;
  resize: vertical;
  transition: border-color 0.2s;
  box-sizing: border-box;
  font-family: inherit;
}

.form-textarea:focus {
  border-color: var(--primary);
}

.form-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.form-hint {
  font-size: 13px;
  color: var(--text-muted);
}

.category-select {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.cat-option {
  padding: 6px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 20px;
  font-size: 13px;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.cat-option:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.cat-option.active {
  background: var(--primary);
  border-color: var(--primary);
  color: #fff;
}

.popup-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #F1F5F9;
}
</style>
