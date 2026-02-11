<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>维修员管理</span>
        <div>
          <el-input v-model="searchText" placeholder="搜索姓名、手机号或账号" style="width: 200px; margin-right: 10px" clearable @input="handleSearch" />
          <el-button type="primary" @click="showAddDialog = true">新增维修员</el-button>
        </div>
      </div>
    </template>

    <el-table :data="filteredWorkerList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="100">
        <template #default="{ row }">
          <el-avatar :src="row.avatar" />
        </template>
      </el-table-column>
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="username" label="账号" width="150" />
      <el-table-column prop="phone" label="联系方式" width="150" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row)">
            {{ getStatusText(row) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 0" type="success" size="small" @click="handleApprove(row.id)">审核通过</el-button>
          <el-button v-if="row.status === 1" type="warning" size="small" @click="handleDisable(row.id)">禁用</el-button>
          <el-button v-if="row.status === 2" type="success" size="small" @click="handleEnable(row.id)">启用</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="showAddDialog" title="新增维修员" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="账号">
          <el-input v-model="formData.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确认</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑维修员" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="formData.password" type="password" placeholder="不修改请留空" />
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
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const uploadUrl = '/api/common/upload'
const workerList = ref([])
const searchText = ref('')
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const formData = ref({
  userId: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  avatar: ''
})

const filteredWorkerList = computed(() => {
  if (!searchText.value) return workerList.value
  const search = searchText.value.toLowerCase()
  return workerList.value.filter(worker =>
    worker.realName?.toLowerCase().includes(search) ||
    worker.phone?.includes(search) ||
    worker.username?.toLowerCase().includes(search)
  )
})

onMounted(() => {
  loadWorkers()
})

const loadWorkers = async () => {
  const res = await request.get('/admin/workers')
  if (res.data) {
    workerList.value = res.data.map(w => ({
      ...w,
      id: w.id || w.userId,
      avatar: w.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'
    }))
  } else {
    workerList.value = []
  }
}

const handleSearch = () => {
  // 搜索由computed自动处理
}

const getStatusText = (row) => {
  return row.status === 0 ? '待审核' : row.status === 1 ? '正常' : '禁用'
}

const getStatusType = (row) => {
  return row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'
}

const beforeAvatarUpload = (rawFile) => {
  const isImage = rawFile.type.startsWith('image/')
  const isLt2M = rawFile.size / 1024 / 1024 < 10
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200 && response.data) {
    formData.value.avatar = response.data.url
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleAdd = async () => {
  if (!formData.value.username || !formData.value.password || !formData.value.realName || !formData.value.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }

  await request.post('/admin/worker/add', formData.value)
  ElMessage.success('添加成功')
  showAddDialog.value = false
  resetFormData()
  loadWorkers()
}

const handleEdit = (row) => {
  formData.value = {
    userId: row.id,
    username: row.username,
    password: '',
    realName: row.realName,
    phone: row.phone,
    avatar: row.avatar
  }
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.realName || !formData.value.phone) {
    ElMessage.warning('请填写必要信息')
    return
  }

  await request.put('/admin/worker/update', formData.value)
  ElMessage.success('更新成功')
  showEditDialog.value = false
  resetFormData()
  loadWorkers()
}

const handleApprove = async (id) => {
  await request.put('/admin/worker/approve', null, { params: { workerId: id, status: 1 } })
  ElMessage.success('审核通过')
  loadWorkers()
}

const handleDisable = async (id) => {
  await request.put('/admin/worker/approve', null, { params: { workerId: id, status: 2 } })
  ElMessage.success('已禁用')
  loadWorkers()
}

const handleEnable = async (id) => {
  await request.put('/admin/worker/approve', null, { params: { workerId: id, status: 1 } })
  ElMessage.success('已启用')
  loadWorkers()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该维修员吗?', '提示', { type: 'warning' })
  await request.delete(`/admin/worker/${id}`)
  ElMessage.success('删除成功')
  loadWorkers()
}

const resetFormData = () => {
  formData.value = {
    userId: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    avatar: ''
  }
}
</script>

<style scoped>
.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>
