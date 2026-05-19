<template>
  <div class="login-container">
    <div class="login-box">
      <h1>智慧社区管理系统</h1>
      
      <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="80px" class="login-form" @submit.prevent="handleLogin">
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入手机号或账号" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        
        <el-form-item label="身份" prop="loginType">
          <el-radio-group v-model="loginForm.loginType">
            <el-radio :label="0">管理员</el-radio>
            <el-radio :label="2">维修员</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item class="button-item">
          <div class="button-group">
            <el-button type="primary" native-type="submit" class="action-btn login-btn">
              登录
            </el-button>
            <el-button @click="showRegisterDialog = true; registerType = 2" class="action-btn register-btn">
              注册维修员
            </el-button>
            <el-button @click="showRegisterDialog = true; registerType = 0" class="action-btn register-btn">
              注册管理员
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog v-model="showRegisterDialog" :title="registerType === 0 ? '注册管理员' : '注册维修员'" width="500px">
      <el-form :model="registerForm" :rules="registerRules" ref="registerFormRef" label-width="80px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :on-success="handleUploadSuccess"
            :show-file-list="false"
            :before-upload="beforeUpload"
          >
            <div v-if="registerForm.avatar" class="avatar-preview">
               <el-avatar :size="80" :src="registerForm.avatar" />
            </div>
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="registerForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请设置密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRegisterDialog = false">取消</el-button>
        <el-button type="primary" @click="handleRegister" class="dialog-confirm-btn">注册</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref()
const registerFormRef = ref()
const showRegisterDialog = ref(false)
const registerType = ref(2)
const uploadUrl = '/api/common/upload'

const loginForm = ref({
  username: '',
  password: '',
  loginType: 0
})

const registerForm = ref({
  username: '',
  realName: '',
  phone: '',
  password: '',
  avatar: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  loginType: [{ required: true, message: '请选择身份', trigger: 'change' }]
}

const registerRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await request.post('/user/login', loginForm.value)
        if (res.code === 200 || res.data) {
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('userInfo', JSON.stringify(res.data))
          ElMessage.success('登录成功')
          if (res.data.role === 0) {
            router.push('/dashboard')
          } else if (res.data.role === 2) {
            router.push('/worker-home')
          }
        } else {
            ElMessage.error(res.msg || '登录失败')
        }
      } catch (error) {
        console.error(error)
      }
    }
  })
}

const handleUploadSuccess = (response) => {
  registerForm.value.avatar = response.data.url
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

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (registerType.value === 0) {
          await request.post('/user/register/admin', registerForm.value)
          ElMessage.success('管理员注册成功')
        } else {
          await request.post('/user/register/worker', registerForm.value)
          ElMessage.success('注册成功，请等待管理员审核')
        }
        showRegisterDialog.value = false
        registerForm.value = { username: '', realName: '', phone: '', password: '', avatar: '' }
      } catch (error) {
        console.error(error)
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1E293B 0%, #0F172A 100%);
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 20px;
  padding: 48px;
  width: 100%;
  max-width: 480px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  transition: all 0.3s ease;
}

.login-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 25px 70px rgba(0, 0, 0, 0.35);
}

.login-box h1 {
  text-align: center;
  margin-bottom: 32px;
  color: var(--text-primary);
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 1px;
}

.button-item {
  margin-top: 30px;
  margin-bottom: 0;
}

.button-item :deep(.el-form-item__content) {
  margin-left: 0 !important;
}

.button-group {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 16px;
}

.action-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: var(--radius-sm);
  margin-left: 0 !important;
}

.login-btn {
  background: var(--primary-gradient) !important;
  border: none !important;
  color: white;
  font-weight: 600;
  box-shadow: 0 4px 14px rgba(79, 110, 247, 0.4);
  transition: all 0.3s;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(79, 110, 247, 0.5);
}

.register-btn {
  border: 1px solid #E2E8F0;
  color: var(--text-secondary);
}

.register-btn:hover {
  color: var(--primary);
  border-color: var(--primary-light);
  background-color: var(--primary-bg);
  transform: translateY(-2px);
}

.dialog-confirm-btn {
  background: var(--primary-gradient) !important;
  border: none !important;
}

.login-container :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(79, 110, 247, 0.2) inset !important;
}

.login-container :deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: var(--primary);
  background: var(--primary);
}

.login-container :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: var(--primary);
}

.avatar-uploader .el-upload {
  border: 1px dashed #E2E8F0;
  border-radius: var(--radius-md);
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #94A3B8;
  width: 80px;
  height: 80px;
  text-align: center;
  line-height: 80px;
  border: 1px dashed #E2E8F0;
  border-radius: var(--radius-sm);
}
</style>