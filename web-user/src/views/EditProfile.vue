<template>
  <div class="edit-profile">
    <div class="page-header">
      <button @click="goBack" class="btn-back">
        <van-icon name="arrow-left" size="20" />
        返回
      </button>
      <h2>编辑个人信息</h2>
    </div>
    
    <div class="content">
      <!-- 头像上传 -->
      <div class="avatar-section">
        <div class="avatar-preview" @click="chooseAvatar">
          <img :src="userInfo.avatar || defaultAvatar" alt="头像" />
          <div class="avatar-upload-btn">
            <van-icon name="camera" size="20" color="#fff" />
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

      <!-- 个人信息 -->
      <van-form @submit="handleSubmit" class="info-form">
        <van-field
          v-model="form.realName"
          name="realName"
          label="姓名"
          placeholder="请输入姓名"
          :rules="[{ required: true, message: '请输入姓名' }]"
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
          :rules="[{ required: true, message: '请输入手机号' }]"
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
        <van-field
          v-model="form.idCard"
          name="idCard"
          label="身份证号"
          placeholder="请输入身份证号"
          size="large"
        />
      </van-form>

      <!-- 修改密码 -->
      <div class="password-section">
        <h3>修改密码</h3>
        <van-form @submit="handleChangePassword" class="password-form">
          <van-field
            v-model="passwordForm.oldPassword"
            type="password"
            name="oldPassword"
            label="原密码"
            placeholder="请输入原密码"
            :rules="[{ required: true, message: '请输入原密码' }]"
            size="large"
          />
          <van-field
            v-model="passwordForm.newPassword"
            type="password"
            name="newPassword"
            label="新密码"
            placeholder="请输入新密码"
            :rules="[{ required: true, message: '请输入新密码' }]"
            size="large"
          />
          <van-field
            v-model="passwordForm.confirmPassword"
            type="password"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入新密码"
            :rules="[{ required: true, message: '请确认新密码' }]"
            size="large"
          />
        </van-form>
      </div>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <van-button round block type="primary" size="large" @click="handleSubmit">
          保存信息
        </van-button>
        <van-button round block type="info" size="large" @click="handleChangePassword">
          修改密码
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const avatarInput = ref(null)
const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20portrait%20professional%20clean%20design&image_size=square'

const userInfo = ref({})
const form = ref({
  realName: '',
  username: '',
  phone: '',
  building: '',
  unit: '',
  room: '',
  idCard: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = async () => {
  try {
    const res = await request.get('/user/info')
    userInfo.value = res.data
    form.value = {
      realName: res.data.realName || '',
      username: res.data.username || '',
      phone: res.data.phone || '',
      building: res.data.building || '',
      unit: res.data.unit || '',
      room: res.data.room || '',
      idCard: res.data.idCard || ''
    }
  } catch (error) {
    console.error('加载用户信息失败', error)
  }
}

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
      
      userInfo.value.avatar = res.data.url
      showToast('头像上传成功')
    } catch (error) {
      showToast('头像上传失败')
    }
  }
}

const handleSubmit = async () => {
  try {
    await request.put('/user/update', {
      ...form.value,
      avatar: userInfo.value.avatar
    })
    
    showToast('信息更新成功')
    setTimeout(() => {
      router.back()
    }, 500)
  } catch (error) {
    showToast(error.message || '信息更新失败')
  }
}

const handleChangePassword = async () => {
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    showToast('两次输入的密码不一致')
    return
  }

  try {
    await request.post('/user/change-password', passwordForm.value)
    
    showToast('密码修改成功')
    passwordForm.value = {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  } catch (error) {
    showToast(error.message || '密码修改失败')
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.edit-profile {
  min-height: 100vh;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.btn-back {
  background: #f0f9ff;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #e6f7ff;
  transform: translateY(-2px);
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #304156;
  margin: 0;
}

.content {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 20px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 32px;
}

.avatar-preview {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  margin: 0 auto;
  cursor: pointer;
  border: 3px solid #409EFF;
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
  background: rgba(64, 158, 255, 0.8);
  color: white;
  padding: 8px 0;
  text-align: center;
  font-size: 12px;
}

.info-form,
.password-form {
  margin-bottom: 32px;
}

.info-form .van-field,
.password-form .van-field {
  margin-bottom: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px 16px;
}

.password-section {
  margin-bottom: 32px;
}

.password-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #304156;
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 32px;
}

.action-buttons .van-button {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}
</style>