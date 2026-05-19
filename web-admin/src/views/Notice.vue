<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>通知公告</span>
        <el-button type="primary" @click="() => { resetFormData(); showAddDialog = true }">发布通知</el-button>
      </div>
    </template>

    <el-table :data="noticeList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" width="333" />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.type === 1 ? 'danger' : 'primary'">
            {{ row.type === 1 ? '紧急' : '普通' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="已读人数" width="100">
        <template #default="{ row }">
          {{ readCountMap[row.id] ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="showAddDialog" title="发布通知" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="formData.type">
            <el-radio :value="0">普通</el-radio>
            <el-radio :value="1">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="formData.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="/api/common/upload"
            :auto-upload="true"
            :multiple="true"
            :on-success="(response, file) => { 
              if (!formData.image) {
                formData.image = response.data.url
              } else {
                formData.image += ',' + response.data.url
              }
              if (imageFileList.value) {
                imageFileList.value.push({name: file.name, url: response.data.url})
              }
            }"
            :on-error="handleUploadError"
            :file-list="imageFileList"
            :on-remove="(file, fileList) => {
              // 更新formData.image
              const remainingUrls = fileList.map(item => item.url).join(',')
              formData.image = remainingUrls
            }"
            :before-upload="(file) => {
              // 确保imageFileList存在
              if (!imageFileList.value) {
                imageFileList.value = []
              }
              return true
            }"
            accept="image/*"
            list-type="picture"
            :class="'image-uploader'"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="/api/common/upload"
            :auto-upload="true"
            :on-success="(response, file) => { 
              formData.attachment = response.data.url 
              attachmentFileList.value = [{name: file.name, url: response.data.url}]
            }"
            :on-error="handleUploadError"
            :file-list="attachmentFileList"
            :on-remove="(file, fileList) => {
              // 更新formData.attachment
              formData.attachment = ''
            }"
          >
            <el-button type="primary">上传附件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">发布</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑通知" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="formData.type">
            <el-radio :value="0">普通</el-radio>
            <el-radio :value="1">紧急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker
            v-model="formData.publishTime"
            type="datetime"
            placeholder="选择发布时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="formData.content" type="textarea" :rows="8" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            action="/api/common/upload"
            :auto-upload="true"
            :multiple="true"
            :on-success="(response, file) => { 
              if (!formData.image) {
                formData.image = response.data.url
              } else {
                formData.image += ',' + response.data.url
              }
              if (imageFileList.value) {
                imageFileList.value.push({name: file.name, url: response.data.url})
              }
            }"
            :on-error="handleUploadError"
            :file-list="imageFileList"
            :on-remove="(file, fileList) => {
              // 更新formData.image
              const remainingUrls = fileList.map(item => item.url).join(',')
              formData.image = remainingUrls
            }"
            :before-upload="(file) => {
              // 确保imageFileList存在
              if (!imageFileList.value) {
                imageFileList.value = []
              }
              return true
            }"
            accept="image/*"
            list-type="picture"
            :class="'image-uploader'"
          >
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="/api/common/upload"
            :auto-upload="true"
            :on-success="(response, file) => { 
              formData.attachment = response.data.url 
              attachmentFileList.value = [{name: file.name, url: response.data.url}]
            }"
            :on-error="handleUploadError"
            :file-list="attachmentFileList"
            :on-remove="(file, fileList) => {
              // 更新formData.attachment
              formData.attachment = ''
            }"
          >
            <el-button type="primary">上传附件</el-button>
          </el-upload>
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
import request from '@/utils/request'

const noticeList = ref([])
const readCountMap = ref({})
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const imageFileList = ref([])
const attachmentFileList = ref([])
const formData = ref({
  id: null,
  title: '',
  type: 0,
  content: '',
  publishTime: '',
  image: '',
  attachment: ''
})

const handleFileChange = (file, fileList) => {
  // 这里可以处理文件上传逻辑
  console.log('File changed:', file)
  console.log('File list:', fileList)
  // 实际项目中，这里应该调用上传接口
}

const handleUploadError = (error) => {
  console.error('文件上传失败:', error)
  ElMessage.error('文件上传失败，请重试')
}

onMounted(() => {
  loadNotices()
})

const loadNotices = async () => {
  const res = await request.get('/notice/list', { params: { page: 1, size: 100 } })
  noticeList.value = res.data.records || []
  loadReadCounts()
}

const loadReadCounts = async () => {
  const map = {}
  const promises = noticeList.value.map(async (notice) => {
    try {
      const res = await request.get(`/notice/${notice.id}/read-status`)
      map[notice.id] = res.data.readCount
    } catch {
      map[notice.id] = 0
    }
  })
  await Promise.all(promises)
  readCountMap.value = map
}

const resetFormData = () => {
  formData.value = {
    id: null,
    title: '',
    type: 0,
    content: '',
    publishTime: '',
    image: '',
    attachment: ''
  }
  // 清空文件列表
  imageFileList.value = []
  attachmentFileList.value = []
}

const handleAdd = async () => {
  if (!formData.value.title || !formData.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    // 格式化日期为后端期望的格式：yyyy-MM-dd HH:mm:ss
    const formatDate = (date) => {
      if (!date) return new Date().toISOString().replace('T', ' ').substring(0, 19);
      const d = new Date(date);
      return d.toISOString().replace('T', ' ').substring(0, 19);
    };
    
    const noticeData = {
      title: formData.value.title,
      type: formData.value.type,
      content: formData.value.content,
      publishTime: formatDate(formData.value.publishTime),
      image: formData.value.image || '',
      attachment: formData.value.attachment || ''
    }
    console.log('发布通知数据:', noticeData)
    await request.post('/notice/add', noticeData)
    ElMessage.success('发布成功')
    showAddDialog.value = false
    resetFormData()
    loadNotices()
  } catch (error) {
    console.error('发布通知失败:', error)
    ElMessage.error('发布失败，请重试')
  }
}

const handleEdit = (row) => {
  formData.value = {
    id: row.id,
    title: row.title,
    type: row.type,
    content: row.content,
    publishTime: row.publishTime,
    image: row.image || '',
    attachment: row.attachment || ''
  }
  
  // 设置图片文件列表
  if (row.image) {
    const images = row.image.split(',').filter(img => img.trim())
    imageFileList.value = images.map(img => ({
      name: img.substring(img.lastIndexOf('/') + 1),
      url: img
    }))
  } else {
    imageFileList.value = []
  }
  
  // 设置附件文件列表
  if (row.attachment) {
    attachmentFileList.value = [{
      name: row.attachment.substring(row.attachment.lastIndexOf('/') + 1),
      url: row.attachment
    }]
  } else {
    attachmentFileList.value = []
  }
  
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.title || !formData.value.content) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    // 格式化日期为后端期望的格式：yyyy-MM-dd HH:mm:ss
    const formatDate = (date) => {
      if (!date) return new Date().toISOString().replace('T', ' ').substring(0, 19);
      const d = new Date(date);
      return d.toISOString().replace('T', ' ').substring(0, 19);
    };
    
    const noticeData = {
      id: formData.value.id,
      title: formData.value.title,
      type: formData.value.type,
      content: formData.value.content,
      publishTime: formatDate(formData.value.publishTime),
      image: formData.value.image || '',
      attachment: formData.value.attachment || ''
    }
    console.log('更新通知数据:', noticeData)
    await request.put('/notice/update', noticeData)
    ElMessage.success('更新成功')
    showEditDialog.value = false
    resetFormData()
    loadNotices()
  } catch (error) {
    console.error('更新通知失败:', error)
    ElMessage.error('更新失败，请重试')
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该通知吗?', '提示', { type: 'warning' })
  await request.delete(`/notice/${id}`)
  ElMessage.success('删除成功')
  loadNotices()
}
</script>

<style scoped>
.image-uploader :deep(.el-upload-list__item-info) {
  display: none !important;
}

.image-uploader :deep(.el-upload-list__item) {
  border: none !important;
  box-shadow: none !important;
  background-color: transparent !important;
  margin-right: 10px;
  width: 120px !important;
  height: 120px !important;
  padding: 0 !important;
}

.image-uploader :deep(.el-upload-list__item-thumbnail) {
  border-radius: 4px;
  width: 100% !important;
  height: 100% !important;
  object-fit: cover;
}

.image-uploader :deep(.el-icon--close) {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: rgba(0, 0, 0, 0.6);
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  font-size: 14px;
  z-index: 10;
  display: block !important;
}

.image-uploader :deep(.el-upload-list__item-status-label) {
  display: none !important;
}

.image-uploader :deep(.el-icon--close-tip) {
  display: none !important;
}
</style>
