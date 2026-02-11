<template>
  <div class="admin-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员账号管理</span>
          <el-button type="primary" size="small" @click="showAddAdminDialogVisible = true">添加管理员</el-button>
        </div>
      </template>
      
      <el-table :data="adminList" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="100">
          <template #default="scope">
            <el-avatar :src="scope.row.avatar" />
          </template>
        </el-table-column>
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="phone" label="联系方式" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="showEditAdminDialog(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteAdmin(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加管理员对话框 -->
    <el-dialog v-model="showAddAdminDialogVisible" title="添加管理员" width="500px">
      <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            :action="uploadUrl"
            :on-success="handleAdminUploadSuccess"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-avatar :size="80" :src="adminForm.avatar">
              <el-icon><Plus /></el-icon>
            </el-avatar>
          </el-upload>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="adminForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="adminForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="adminForm.password" type="password" placeholder="请设置密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddAdminDialog = false">取消</el-button>
        <el-button type="primary" @click="addAdmin">添加</el-button>
      </template>
    </el-dialog>

    <!-- 编辑管理员对话框 -->
    <el-dialog v-model="showEditAdminDialogVisible" title="编辑管理员" width="500px">
      <el-form :model="adminForm" :rules="adminRules" ref="adminFormRef" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            :action="uploadUrl"
            :on-success="handleAdminUploadSuccess"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <el-avatar :size="80" :src="adminForm.avatar">
              <el-icon><Plus /></el-icon>
            </el-avatar>
          </el-upload>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="adminForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="adminForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="adminForm.phone" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="adminForm.password" type="password" placeholder="请设置密码（留空表示不修改）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditAdminDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="updateAdmin">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, UserFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

const adminFormRef = ref()

// 管理员相关
const adminList = ref([])
const showAddAdminDialogVisible = ref(false)
const showEditAdminDialogVisible = ref(false)
const uploadUrl = '/api/common/upload'

const adminForm = ref({
  id: null,
  username: '',
  realName: '',
  phone: '',
  password: '',
  avatar: ''
})

const adminRules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { 
      required: true, 
      message: '请设置密码', 
      trigger: 'blur',
      validator: (rule, value, callback) => {
        if (adminForm.value.id && !value) {
          callback()
        } else if (!value) {
          callback(new Error('请设置密码'))
        } else {
          callback()
        }
      }
    }
  ]
}

onMounted(() => {
  loadAdminList()
})

const loadAdminList = async () => {
  try {
    const res = await request.get('/admin/list')
    if (res.code === 200 && res.data) {
      adminList.value = res.data
    }
  } catch (error) {
    console.error('加载管理员列表失败', error)
    adminList.value = []
  }
}

// 管理员管理方法
const showEditAdminDialog = (admin) => {
  adminForm.value = { ...admin }
  showEditAdminDialogVisible.value = true
}

const handleAdminUploadSuccess = (response) => {
  adminForm.value.avatar = response.data.url
  ElMessage.success('上传成功')
}

const beforeUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 10
  if (!isJPG) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 10MB!')
    return false
  }
  return true
}

const addAdmin = async () => {
  await adminFormRef.value.validate()
  try {
    const res = await request.post('/admin/add', adminForm.value)
    if (res.code === 200) {
      ElMessage.success('添加成功')
      showAddAdminDialogVisible.value = false
      adminForm.value = {
        id: null,
        username: '',
        realName: '',
        phone: '',
        password: '',
        avatar: ''
      }
      await loadAdminList()
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (error) {
    ElMessage.error('添加失败：' + (error.message || '未知错误'))
  }
}

const updateAdmin = async () => {
  await adminFormRef.value.validate()
  try {
    const updateData = {
      ...adminForm.value,
      userId: adminForm.value.id
    }
    const res = await request.put('/admin/update', updateData)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      showEditAdminDialogVisible.value = false
      await loadAdminList()
    } else {
      ElMessage.error(res.message || '更新失败')
    }
  } catch (error) {
    ElMessage.error('更新失败：' + (error.message || '未知错误'))
  }
}

const deleteAdmin = async (id) => {
  try {
    const res = await request.delete(`/admin/delete/${id}`)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await loadAdminList()
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    ElMessage.error('删除失败：' + (error.message || '未知错误'))
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
</style>
