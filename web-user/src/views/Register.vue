<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <van-icon name="user-circle-o" size="64" color="#304156" />
        <h1>注册账号</h1>
        <p>成为智慧社区的一员</p>
      </div>

      <van-form @submit="handleRegister" class="register-form">
        <div class="avatar-upload">
          <div class="avatar-preview" @click="chooseAvatar">
            <img :src="avatar || defaultAvatar" alt="头像" />
            <div class="avatar-upload-btn">
              <van-icon name="plus" size="24" color="#fff" />
            </div>
          </div>
          <input
            type="file"
            ref="avatarInput"
            accept="image/*"
            style="display: none"
            @change="handleAvatarUpload"
          />
        </div>

        <van-field
          v-model="form.realName"
          name="realName"
          label="真实姓名"
          placeholder="请输入真实姓名"
          :rules="[{ required: true, message: '请输入真实姓名' }]"
          size="large"
        />
        <van-field
          v-model="form.username"
          name="username"
          label="用户名"
          placeholder="请输入用户名"
          :rules="[{ required: true, message: '请输入用户名' }]"
          size="large"
        />
        <van-field
          v-model="form.phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号' }]"
          size="large"
        />
        <van-field
          v-model="form.idCard"
          name="idCard"
          label="身份证号"
          placeholder="请输入身份证号"
          :rules="[{ required: true, message: '请输入身份证号' }]"
          size="large"
        />
        <van-field
          v-model="form.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          size="large"
        />
        <van-field
          v-model="form.confirmPassword"
          type="password"
          name="confirmPassword"
          label="确认密码"
          placeholder="请再次输入密码"
          :rules="[{ required: true, message: '请确认密码' }]"
          size="large"
        />

        <van-field
          v-model="form.building"
          name="building"
          label="楼栋"
          placeholder="请输入楼栋号"
          :rules="[{ required: true, message: '请输入楼栋号' }]"
          size="large"
        />
        <van-field
          v-model="form.unit"
          name="unit"
          label="单元"
          placeholder="请输入单元号"
          :rules="[{ required: true, message: '请输入单元号' }]"
          size="large"
        />
        <van-field
          v-model="form.room"
          name="room"
          label="房号"
          placeholder="请输入房号"
          :rules="[{ required: true, message: '请输入房号' }]"
          size="large"
        />

        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" size="large" class="register-btn">
            注册
          </van-button>
          <router-link to="/login" class="login-link">
            已有账号？立即登录
          </router-link>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const avatarInput = ref(null)
const avatar = ref('')
const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20portrait%20professional%20clean%20design&image_size=square'

const form = ref({
  realName: '',
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  building: '',
  unit: '',
  room: '',
  idCard: ''
})

const chooseAvatar = () => {
  avatarInput.value.click()
}

const handleAvatarUpload = async (e) => {
  const file = e.target.files[0]
  if (file) {
    try {
      const formData = new FormData()
      formData.append('file', file)
      
      const res = await request.post('/common/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })
      
      avatar.value = res.data.url
      showToast('头像上传成功')
    } catch (error) {
      showToast('头像上传失败')
    }
  }
}

const handleRegister = async (values) => {
  if (form.value.password !== form.value.confirmPassword) {
    showToast('两次输入的密码不一致')
    return
  }

  try {
    const res = await request.post('/user/register', {
      ...form.value,
      avatar: avatar.value,
      role: 1 // 业主
    })
    
    showToast('注册成功')
    setTimeout(() => {
      router.replace('/login')
    }, 1000)
  } catch (error) {
    showToast(error.message || '注册失败')
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  /* 背景改为深色主题渐变 */
  background: linear-gradient(135deg, #304156 0%, #345473 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.register-box {
  background: white;
  border-radius: 16px;
  padding: 40px 32px; /* 调整内边距，更紧凑 */
  width: 100%;
  max-width: 480px; /* 稍微调小一点最大宽度，适配移动端 */
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #304156; /* 标题颜色 */
  margin: 16px 0 8px;
}

.register-header p {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

.register-form {
  margin-bottom: 32px;
}

.avatar-upload {
  text-align: center;
  margin-bottom: 32px;
}

.avatar-preview {
  position: relative;
  width: 100px; /* 稍微缩小头像尺寸 */
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto;
  cursor: pointer;
  /* 边框颜色改为主题色 */
  border: 3px solid #304156; 
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-upload-btn {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  /* 背景改为主题色半透明 */
  background: rgba(48, 65, 86, 0.85); 
  color: white;
  padding: 6px 0;
  text-align: center;
  font-size: 12px;
}

.register-form .van-field {
  margin-bottom: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px 16px;
}

.form-actions {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 注册按钮强制样式 */
.register-btn {
  background: #304156 !important;
  border-color: #304156 !important;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

.register-btn:active {
  opacity: 0.9;
}

.login-link {
  text-align: center;
  color: #345473; /* 链接颜色使用较浅的主题色 */
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
}

.login-link:hover {
  text-decoration: underline;
}
</style>