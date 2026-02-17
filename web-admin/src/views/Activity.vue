<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>社区活动</span>
        <el-button type="primary" @click="showAddDialog = true">发布活动</el-button>
      </div>
    </template>

    <el-table :data="activityList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面" width="160">
        <template #default="{ row }">
          <el-image style="width: 100px; height: 60px" :src="row.coverImage" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="标题" width="200" />
      <el-table-column prop="location" label="地点" width="260" />
      <el-table-column prop="startTime" label="开始时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="showAddDialog" title="发布活动" width="600px" @open="resetFormData">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="formData.coverImage" :src="formData.coverImage" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="formData.description" type="textarea" :rows="6" placeholder="请输入活动内容" />
        </el-form-item>
        <el-form-item label="活动图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :multiple="true"
            :limit="10"
            :on-success="handleMultipleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <el-button type="primary">上传图片</el-button>
            <template #tip>
              <div class="el-upload__tip">
                最多上传10张图片，单张不超过20MB
              </div>
            </template>
          </el-upload>
          <div v-if="formData.images && formData.images.length > 0" class="uploaded-images">
            <div v-for="(img, index) in formData.images" :key="index" class="uploaded-image-item">
              <img :src="img" class="uploaded-image-preview" />
              <el-button type="danger" size="small" @click="removeImage(index)">删除</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">发布</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑活动" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入活动标题" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="formData.coverImage" :src="formData.coverImage" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="formData.location" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="formData.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker
            v-model="formData.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="活动内容">
          <el-input v-model="formData.description" type="textarea" :rows="6" placeholder="请输入活动内容" />
        </el-form-item>
        <el-form-item label="活动图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :multiple="true"
            :limit="10"
            :on-success="handleMultipleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <el-button type="primary">上传图片</el-button>
            <template #tip>
              <div class="el-upload__tip">
                最多上传10张图片，单张不超过20MB
              </div>
            </template>
          </el-upload>
          <div v-if="formData.images && formData.images.length > 0" class="uploaded-images">
            <div v-for="(img, index) in formData.images" :key="index" class="uploaded-image-item">
              <img :src="img" class="uploaded-image-preview" />
              <el-button type="danger" size="small" @click="removeImage(index)">删除</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">更新</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const uploadUrl = '/api/common/upload'
const activityList = ref([])
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const formData = ref({
  id: null,
  title: '',
  coverImage: '',
  location: '',
  startTime: '',
  endTime: '',
  description: '',
  images: []
})

onMounted(() => {
  loadActivities()
})

const loadActivities = async () => {
  const res = await request.get('/activity/list', { params: { page: 1, size: 100 } })
  activityList.value = res.data.records || []
}

const resetFormData = () => {
  formData.value = {
    id: null,
    title: '',
    coverImage: '',
    location: '',
    startTime: '',
    endTime: '',
    description: '',
    images: []
  }
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

const handleImageSuccess = (response) => {
  if (response.code === 200 && response.data) {
    formData.value.coverImage = response.data.url
    ElMessage.success('图片上传成功')
  } else if (response.url) {
    // 兼容不同的返回格式
    formData.value.coverImage = response.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const handleMultipleImageSuccess = (response) => {
  if (response.code === 200 && response.data) {
    formData.value.images.push(response.data.url)
    ElMessage.success('图片上传成功')
  } else if (response.url) {
    // 兼容不同的返回格式
    formData.value.images.push(response.url)
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const removeImage = (index) => {
  formData.value.images.splice(index, 1)
}

const formatDate = (date) => {
  if (!date) return null
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

const handleAdd = async () => {
  if (!formData.value.title || !formData.value.location || !formData.value.startTime) {
    ElMessage.warning('请填写必要信息')
    return
  }

  const data = {
    ...formData.value,
    startTime: formatDate(formData.value.startTime),
    endTime: formatDate(formData.value.endTime),
    images: formData.value.images.length > 0 ? JSON.stringify(formData.value.images) : null
  }

  await request.post('/activity/add', data)
  ElMessage.success('发布成功')
  showAddDialog.value = false
  resetFormData()
  loadActivities()
}

const handleEdit = (row) => {
  formData.value = {
    id: row.id,
    title: row.title,
    coverImage: row.coverImage,
    location: row.location,
    startTime: row.startTime,
    endTime: row.endTime,
    description: row.description,
    images: row.images ? JSON.parse(row.images) : []
  }
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.title || !formData.value.location || !formData.value.startTime) {
    ElMessage.warning('请填写必要信息')
    return
  }

  const data = {
    ...formData.value,
    startTime: formatDate(formData.value.startTime),
    endTime: formatDate(formData.value.endTime),
    images: formData.value.images.length > 0 ? JSON.stringify(formData.value.images) : null
  }

  await request.put('/activity/update', data)
  ElMessage.success('更新成功')
  showEditDialog.value = false
  resetFormData()
  loadActivities()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该活动吗?', '提示', { type: 'warning' })
  await request.delete(`/activity/${id}`)
  ElMessage.success('删除成功')
  loadActivities()
}
</script>

<style scoped>
.image-uploader {
  position: relative;
  overflow: hidden;
  display: inline-block;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
}

.uploaded-images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
}

.uploaded-image-item {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.uploaded-image-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.uploaded-image-item .el-button {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  border-radius: 0;
}
</style>
