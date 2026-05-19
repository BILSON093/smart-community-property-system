<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>业主管理</span>
          </div>
          <div class="page-actions">
            <el-input v-model="searchText" placeholder="搜索姓名、手机号或账号" style="width: 200px" clearable @input="handleSearch" />
            <el-button type="primary" @click="showAddDialog = true">新增业主</el-button>
          </div>
        </div>
      </template>

      <el-table :data="filteredOwnerList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="头像" width="100">
        <template #default="{ row }">
          <el-avatar :src="row.avatar" />
        </template>
      </el-table-column>
      <el-table-column prop="username" label="账号" width="150" />
      <el-table-column prop="realName" label="姓名" width="120" />
      <el-table-column prop="phone" label="联系方式" width="150" />
      <el-table-column label="家庭住址" width="200">
        <template #default="{ row }">
          {{ row.building }} {{ row.unit }} {{ row.room }}
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
    <el-dialog v-model="showAddDialog" title="新增业主" width="500px">
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
        <el-form-item label="身份证号">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
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
        <el-form-item label="楼栋">
          <el-input v-model="formData.building" placeholder="例如：A栋" />
        </el-form-item>
        <el-form-item label="单元">
          <el-input v-model="formData.unit" placeholder="例如：1单元" />
        </el-form-item>
        <el-form-item label="房号">
          <el-input v-model="formData.room" placeholder="例如：101" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确认</el-button>
      </template>
    </el-dialog>

    <!-- 编辑对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑业主" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="账号">
          <el-input v-model="formData.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
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
        <el-form-item label="楼栋">
          <el-input v-model="formData.building" placeholder="例如：A栋" />
        </el-form-item>
        <el-form-item label="单元">
          <el-input v-model="formData.unit" placeholder="例如：1单元" />
        </el-form-item>
        <el-form-item label="房号">
          <el-input v-model="formData.room" placeholder="例如：101" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确认</el-button>
      </template>
    </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const uploadUrl = '/api/common/upload'
const ownerList = ref([])
const searchText = ref('')
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const formData = ref({
  userId: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  avatar: '',
  building: '',
  unit: '',
  room: '',
  idCard: ''
})

const filteredOwnerList = computed(() => {
  if (!searchText.value) return ownerList.value
  const search = searchText.value.toLowerCase()
  return ownerList.value.filter(owner =>
    owner.realName?.toLowerCase().includes(search) ||
    owner.phone?.includes(search) ||
    owner.username?.toLowerCase().includes(search)
  )
})

onMounted(() => {
  loadOwners()
})

const loadOwners = async () => {
  const res = await request.get('/admin/owners')
  if (res.data) {
    ownerList.value = res.data.map(o => ({
      ...o,
      id: o.id || o.userId
    }))
  } else {
    ownerList.value = []
  }
}

const handleSearch = () => {
  // 搜索由computed自动处理
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

  await request.post('/admin/owner/add', formData.value)
  ElMessage.success('添加成功')
  showAddDialog.value = false
  resetFormData()
  loadOwners()
}

const handleEdit = (row) => {
  formData.value = {
    userId: row.id,
    username: row.username,
    password: '',
    realName: row.realName,
    phone: row.phone,
    avatar: row.avatar,
    building: row.building || '',
    unit: row.unit || '',
    room: row.room || '',
    idCard: row.idCard || ''
  }
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.realName || !formData.value.phone) {
    ElMessage.warning('请填写必要信息')
    return
  }

  await request.put('/admin/owner/update', formData.value)
  ElMessage.success('更新成功')
  showEditDialog.value = false
  resetFormData()
  loadOwners()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该业主吗?', '提示', { type: 'warning' })
  await request.delete(`/admin/owner/${id}`)
  ElMessage.success('删除成功')
  loadOwners()
}

const resetFormData = () => {
  formData.value = {
    userId: null,
    username: '',
    password: '',
    realName: '',
    phone: '',
    avatar: '',
    building: '',
    unit: '',
    room: '',
    idCard: ''
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
