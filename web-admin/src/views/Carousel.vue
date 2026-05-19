<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>轮播图管理</span>
          </div>
          <div class="page-actions">
            <el-button type="primary" @click="showAddDialog = true">添加轮播图</el-button>
          </div>
        </div>
      </template>

      <el-table :data="carouselList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="300">
        <template #default="{ row }">
          <el-image style="width: 180px; height: 80px" :src="row.imageUrl" fit="cover" />
        </template>
      </el-table-column>

      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="是否展示" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isShow === 1 ? 'success' : 'info'">
            {{ row.isShow === 1 ? '展示' : '隐藏' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="showAddDialog" title="添加轮播图" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="formData.imageUrl" :src="formData.imageUrl" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="是否展示">
          <el-switch v-model="formData.isShow" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确认</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑轮播图" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="formData.imageUrl" :src="formData.imageUrl" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="是否展示">
          <el-switch v-model="formData.isShow" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">更新</el-button>
      </template>
    </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const uploadUrl = '/api/common/upload'
const carouselList = ref([])
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const formData = ref({
  id: null,
  imageUrl: '',
  sortOrder: 0,
  isShow: 1
})

onMounted(() => {
  loadCarousels()
})

const loadCarousels = async () => {
  const res = await request.get('/carousel/list')
  carouselList.value = res.data || []
}

const resetFormData = () => {
  formData.value = {
    id: null,
    imageUrl: '',
    sortOrder: 0,
    isShow: 1
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
    formData.value.imageUrl = response.data.url
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error(response.message || '图片上传失败')
  }
}

const handleAdd = async () => {
  if (!formData.value.imageUrl) {
    ElMessage.warning('请上传图片')
    return
  }

  await request.post('/carousel/add', formData.value)
  ElMessage.success('添加成功')
  showAddDialog.value = false
  resetFormData()
  loadCarousels()
}

const handleEdit = (row) => {
  formData.value = {
    id: row.id,
    imageUrl: row.imageUrl,
    sortOrder: row.sortOrder,
    isShow: row.isShow
  }
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.imageUrl) {
    ElMessage.warning('请上传图片')
    return
  }

  await request.put('/carousel/update', formData.value)
  ElMessage.success('更新成功')
  showEditDialog.value = false
  resetFormData()
  loadCarousels()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该轮播图吗?', '提示', { type: 'warning' })
  await request.delete(`/carousel/${id}`)
  ElMessage.success('删除成功')
  loadCarousels()
}
</script>

<style scoped>
.image-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 200px;
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--el-transition-duration-fast);
}

.image-uploader:hover {
  border-color: var(--el-color-primary);
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
