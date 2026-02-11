<template>
  <div class="login-container">
    <div class="login-box">
      <h1>智慧社区管理系统</h1>
      
      <el-form :model="loginForm" :rules="rules" ref="formRef" label-width="80px" class="login-form">
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
            <el-button type="primary" @click="handleLogin" class="action-btn login-btn">
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
          <el-input v-model="registerForm.phone" placeholder="请输入shou" />
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
  username: 'admin',
  password: '12345678',
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
/* 定义主题变量，方便统一管理 */
.login-container {
  --theme-color: #304156;
  --theme-light: #435a76;
  --theme-dark: #1f2d3d;
  
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  /* 背景改为 304156 渐变 */
  background: linear-gradient(135deg, var(--theme-color) 0%, var(--theme-dark) 100%);
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: 16px;
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
  /* 标题颜色改为主题色 */
  color: #304156; 
  font-size: 26px;
  font-weight: 700;
  letter-spacing: 1px;
}

/* 按钮区域 */
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
  border-radius: 8px;
  margin-left: 0 !important;
}

/* 登录按钮 - 使用 #304156 */
.login-btn {
  background-color: #304156;
  border-color: #304156;
  color: white;
  font-weight: 600;
  transition: all 0.3s;
}

.login-btn:hover {
  /* 稍微变亮一点，增加交互感 */
  background-color: #435a76;
  border-color: #435a76;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(48, 65, 86, 0.3);
}

/* 注册按钮 - 样式调整 */
.register-btn {
  border: 1px solid #dcdfe6;
  color: #606266;
}

.register-btn:hover {
  color: #304156;
  border-color: #304156;
  background-color: #f0f2f5;
  transform: translateY(-2px);
}

/* 弹窗中的确认按钮也同步主题色 */
.dialog-confirm-btn {
  background-color: #304156;
  border-color: #304156;
}
.dialog-confirm-btn:hover {
  background-color: #435a76;
  border-color: #435a76;
}

/* 覆盖 Element Plus 默认的 Input Focus 颜色 */
.login-container :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #304156 inset;
}

.login-container :deep(.el-radio__input.is-checked .el-radio__inner) {
  border-color: #304156;
  background: #304156;
}

.login-container :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #304156;
}

/* 头像上传样式 */
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: #304156;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 80px;
  height: 80px;
  text-align: center;
  line-height: 80px;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
}
</style>