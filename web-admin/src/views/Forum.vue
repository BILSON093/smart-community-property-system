<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>论坛管理</span>
        <el-button type="primary" @click="showAddDialog = true">发布帖子</el-button>
      </div>
    </template>

    <div style="margin-bottom: 20px; display: flex; gap: 16px; align-items: center">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索帖子标题或内容"
        style="width: 400px"
        clearable
        @clear="loadForums"
        @keyup.enter="loadForums"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="selectedCategory" placeholder="全部分类" style="width: 200px" clearable @change="loadForums">
        <el-option label="全部分类" :value="null" />
        <el-option v-for="category in categoryList" :key="category.id" :label="category.name" :value="category.id" />
      </el-select>
      <el-button type="primary" @click="loadForums">搜索</el-button>
    </div>

    <el-table :data="forumList" border stripe>
      <el-table-column label="发布者" width="160">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; gap: 8px">
            <el-avatar :size="28" :src="row.userAvatar">
              {{ (row.userName || '用户').charAt(0) }}
            </el-avatar>
            <span>{{ row.userName || '匿名' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" min-width="160">
        <template #default="{ row }">
          <div style="display: flex; align-items: center; gap: 6px">
            <el-tag v-if="row.isPinned" type="danger" size="small">置顶</el-tag>
            <span>{{ row.title }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="categoryName" label="分类" width="85" />
      <el-table-column prop="content" label="内容" min-width="200">
        <template #default="{ row }">
          <span>{{ row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图片" width="80">
        <template #default="{ row }">
          <el-image
            v-if="row.images"
            style="width: 50px; height: 50px"
            :src="getImage(row.images)"
            :preview-src-list="getImages(row.images)"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="65">
        <template #default="{ row }">
          <el-tag :type="row.isPublic === 1 ? 'success' : 'warning'" size="small">
            {{ row.isPublic === 1 ? '实名' : '匿名' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="互动" width="90">
        <template #default="{ row }">
          <div style="display: flex; gap: 8px">
            <span><el-icon><ChatDotRound /></el-icon> {{ row.commentCount || 0 }}</span>
            <span><el-icon><Star /></el-icon> {{ row.likeCount || 0 }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="160" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button v-if="!row.isPinned" type="warning" size="small" @click="handlePin(row.id)">置顶</el-button>
          <el-button v-else type="info" size="small" @click="handleUnpin(row.id)">取消置顶</el-button>
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" size="small" @click="handleViewComments(row)">评论({{ row.commentCount || 0 }})</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadForums"
      @current-change="loadForums"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <el-dialog v-model="showAddDialog" title="发布帖子" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :show-file-list="true"
            :file-list="imageList"
            :http-request="customUpload"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            :on-remove="handleImageRemove"
            list-type="picture-card"
            multiple
          >
            <el-icon class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="是否匿名">
          <el-switch v-model="formData.isPublic" :active-value="1" :inactive-value="0" />
          <span style="margin-left: 10px; color: #909399">{{ formData.isPublic === 1 ? '实名' : '匿名' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">发布</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEditDialog" title="编辑帖子" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categoryList"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :show-file-list="true"
            :file-list="imageList"
            :http-request="customUpload"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
            :on-remove="handleImageRemove"
            list-type="picture-card"
            multiple
          >
            <el-icon class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="是否匿名">
          <el-switch v-model="formData.isPublic" :active-value="1" :inactive-value="0" />
          <span style="margin-left: 10px; color: #909399">{{ formData.isPublic === 1 ? '实名' : '匿名' }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">更新</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showCommentDialog" title="评论列表" width="800px">
      <div style="max-height: 400px; overflow-y: auto; margin-bottom: 20px">
        <el-empty v-if="commentList.length === 0" description="暂无评论" />
        <div v-for="comment in commentList" :key="comment.id" style="display: flex; gap: 12px; margin-bottom: 20px; padding: 16px; background: #f5f7fa; border-radius: 8px">
          <el-avatar :size="40" :src="comment.isPublic === 1 ? comment.userAvatar : ''">
            {{ comment.isPublic === 1 ? (comment.userName || '用户').charAt(0) : '匿' }}
          </el-avatar>
          <div style="flex: 1">
            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px">
              <span style="font-weight: 600; color: #303133">{{ comment.isPublic === 1 ? comment.userName : '匿名用户' }}</span>
              <span style="font-size: 12px; color: #909399">{{ comment.createTime }}</span>
            </div>
            <div style="color: #606266; line-height: 1.6">{{ comment.content }}</div>
            <div v-if="comment.images" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(60px, 1fr)); gap: 8px; margin-top: 8px">
              <el-image
                v-for="(img, index) in getImages(comment.images)"
                :key="index"
                :src="img"
                style="width: 100%; aspect-ratio: 1; border-radius: 4px"
                fit="cover"
                :preview-src-list="getImages(comment.images)"
                :initial-index="index"
              />
            </div>
            <div style="margin-top: 8px">
              <el-button @click="handleDeleteComment(comment)" type="danger" link size="small">
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
      <div style="border-top: 1px solid #e4e7ed; padding-top: 20px">
        <el-form :model="commentForm" label-width="80px">
          <el-form-item label="内容">
            <el-input v-model="commentForm.content" type="textarea" :rows="3" placeholder="输入评论内容..." />
          </el-form-item>
          <el-form-item label="图片">
            <el-upload
              class="image-uploader"
              :show-file-list="true"
              :file-list="commentImageList"
              :http-request="customUpload"
              :on-success="handleCommentImageSuccess"
              :before-upload="beforeImageUpload"
              :on-remove="handleCommentImageRemove"
              list-type="picture-card"
              multiple
            >
              <el-icon class="image-uploader-icon"><Plus /></el-icon>
            </el-upload>
          </el-form-item>
          <el-form-item label="是否匿名">
            <el-switch v-model="commentForm.isAnonymous" :active-value="1" :inactive-value="0" />
            <span style="margin-left: 10px; color: #909399">{{ commentForm.isAnonymous === 1 ? '匿名' : '实名' }}</span>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showCommentDialog = false">关闭</el-button>
        <el-button type="primary" @click="handleAddComment">发表评论</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ChatDotRound, Star, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const forumList = ref([])
const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const searchKeyword = ref('')
const selectedCategory = ref(null)
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const showCommentDialog = ref(false)
const imageList = ref([])
const commentList = ref([])
const commentImageList = ref([])
const currentForumId = ref(null)
const formData = ref({
  id: null,
  userId: null,
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

onMounted(() => {
  loadForums()
  loadCategories()
})

const loadForums = async () => {
  const params = {
    page: currentPage.value,
    size: pageSize.value
  }
  if (searchKeyword.value) {
    params.keyword = searchKeyword.value
  }
  if (selectedCategory.value) {
    params.categoryId = selectedCategory.value
  }
  const res = await request.get('/forum/list', { params })
  forumList.value = res.data.records || []
  total.value = res.data.total || 0
}

const loadCategories = async () => {
  const res = await request.get('/forum/category/list')
  categoryList.value = (res.data || []).filter(c => c.id != null)
}

const resetFormData = () => {
  formData.value = {
    id: null,
    categoryId: null,
    title: '',
    content: '',
    images: '',
    isPublic: 1
  }
  imageList.value = []
}

const resetCommentForm = () => {
  commentForm.value = {
    content: '',
    images: '',
    isAnonymous: 0
  }
  commentImageList.value = []
}

const beforeImageUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt5M = rawFile.size / 1024 / 1024 < 20
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 20MB!')
    return false
  }
  return true
}

const customUpload = async (options) => {
  const { file, onSuccess, onError } = options
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await request.post('/common/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    // Use the server-returned URL directly
    const fileUrl = res.data.url
    file.url = fileUrl
    file.status = 'success'
    onSuccess(res, file)
  } catch (error) {
    onError(error)
  }
}

const handleImageSuccess = (response, file, fileList) => {
  file.status = 'success'
  imageList.value = fileList
  ElMessage.success('图片上传成功')
}

const handleImageRemove = (file, fileList) => {
  imageList.value = fileList
}

const handleCommentImageSuccess = (response, file, fileList) => {
  file.status = 'success'
  commentImageList.value = fileList
  ElMessage.success('图片上传成功')
}

const handleCommentImageRemove = (file, fileList) => {
  commentImageList.value = fileList
}

const handleAdd = async () => {
  if (!formData.value.content) {
    ElMessage.warning('请填写必要信息')
    return
  }

  const imagesArray = imageList.value
    .filter(item => (item.raw?.url || item.url) && item.status === 'success')
    .map(item => item.raw?.url || item.url)

  console.log('imageList:', JSON.stringify(imageList.value))
  console.log('imagesArray:', JSON.stringify(imagesArray))

  if (imagesArray.length > 0 && imagesArray.length !== imageList.value.length) {
    ElMessage.warning('请等待图片上传完成')
    return
  }

  const postData = {
    categoryId: formData.value.categoryId,
    title: formData.value.title,
    content: formData.value.content,
    images: imagesArray.length > 0 ? JSON.stringify(imagesArray) : '[]',
    isPublic: formData.value.isPublic
  }
  console.log(' posting data:', JSON.stringify(postData))
  await request.post('/forum/add', postData)

  ElMessage.success('发布成功')
  showAddDialog.value = false
  resetFormData()
  loadForums()
}

const handleEdit = (row) => {
  formData.value = {
    id: row.id,
    userId: row.userId,
    categoryId: row.categoryId,
    title: row.title,
    content: row.content,
    images: row.images,
    isPublic: row.isPublic
  }
  // Convert existing image URLs to proper file objects for the upload component
  imageList.value = row.images ? JSON.parse(row.images).map(url => ({
    url: url,
    status: 'success'
  })) : []
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.content) {
    ElMessage.warning('请填写必要信息')
    return
  }

  const imagesArray = imageList.value
    .filter(item => (item.raw?.url || item.url) && item.status === 'success')
    .map(item => item.raw?.url || item.url)

  if (imagesArray.length > 0 && imagesArray.length !== imageList.value.length) {
    ElMessage.warning('请等待图片上传完成')
    return
  }

  await request.put('/forum/update', {
    id: formData.value.id,
    categoryId: formData.value.categoryId,
    title: formData.value.title,
    content: formData.value.content,
    images: imagesArray.length > 0 ? JSON.stringify(imagesArray) : '[]',
    isPublic: formData.value.isPublic
  })

  ElMessage.success('更新成功')
  showEditDialog.value = false
  resetFormData()
  loadForums()
}

const getImage = (images) => {
  if (!images) return ''
  const arr = JSON.parse(images)
  return arr[0]
}

const getImages = (images) => {
  if (!images) return []
  return JSON.parse(images)
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该帖子吗?', '提示', { type: 'warning' })
  await request.delete(`/forum/${id}`)
  ElMessage.success('删除成功')
  loadForums()
}

const handlePin = async (id) => {
  await request.put(`/forum/${id}/pin`)
  ElMessage.success('置顶成功')
  loadForums()
}

const handleUnpin = async (id) => {
  await request.put(`/forum/${id}/unpin`)
  ElMessage.success('取消置顶成功')
  loadForums()
}

const handleViewComments = async (row) => {
  try {
    currentForumId.value = row.id
    const res = await request.get(`/forum/${row.id}/comments`, { params: { page: 1, size: 100 } })
    commentList.value = res.data.records || []
    showCommentDialog.value = true
    resetCommentForm()
  } catch (error) {
    ElMessage.error('加载评论失败')
  }
}

const handleDeleteComment = async (comment) => {
  try {
    await request.delete(`/forum/comments/${comment.id}`)
    ElMessage.success('删除成功')
    // Reload comments for the current forum
    const res = await request.get(`/forum/${currentForumId.value}/comments`, { params: { page: 1, size: 100 } })
    commentList.value = res.data.records || []
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const handleAddComment = async () => {
  if (!commentForm.value.content.trim() && commentImageList.value.length === 0) {
    ElMessage.warning('请输入评论内容或上传图片')
    return
  }

  try {
    const imagesArray = commentImageList.value
      .filter(item => item.url && item.status === 'success')
      .map(item => item.url)
    await request.post(`/forum/${currentForumId.value}/comments`, {
      content: commentForm.value.content,
      images: imagesArray.length > 0 ? JSON.stringify(imagesArray) : '[]',
      isPublic: commentForm.value.isAnonymous ? 0 : 1
    })
    ElMessage.success('评论成功')
    resetCommentForm()
    await loadComments(currentForumId.value)
    loadForums()
  } catch (error) {
    ElMessage.error('评论失败')
  }
}

const loadComments = async (forumId) => {
  try {
    const res = await request.get(`/forum/${forumId}/comments`, { params: { page: 1, size: 100 } })
    commentList.value = res.data.records || []
  } catch (error) {
    ElMessage.error('加载评论失败')
  }
}
</script>

<style scoped>
.image-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  transition: var(--el-transition-duration-fast);
}

.image-uploader:hover {
  border-color: var(--el-color-primary);
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  width: 100%;
}
</style>
