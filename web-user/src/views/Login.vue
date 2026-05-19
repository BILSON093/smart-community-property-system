<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <van-icon name="user-circle-o" size="64" color="#304156" />
        <h1>智慧社区</h1>
        <p>欢迎登录物业管理系统</p>
      </div>

      <van-form @submit="handleLogin" class="login-form">
        <van-field
          v-model="form.username"
          name="username"
          label="账号"
          placeholder="请输入手机号或用户名"
          :rules="[{ required: true, message: '请输入账号' }]"
          size="large"
        />
        <van-field
          v-model="form.password"
          :type="showPassword ? 'text' : 'password'"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
          size="large"
        >
          <template #button>
            <van-icon 
              :name="showPassword ? 'eye-o' : 'closed-eye'" 
              @click="showPassword = !showPassword"
              size="18"
              color="#999"
            />
          </template>
        </van-field>

        <div class="form-actions">
          <van-button round block type="primary" native-type="submit" size="large" class="login-btn">
            登录
          </van-button>
          <router-link to="/register" class="register-link">
            还没有账号？立即注册
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
const form = ref({
  username: '',
  password: ''
})
const showPassword = ref(false)

const handleLogin = async (values) => {
  try {
    const res = await request.post('/user/login', {
      ...form.value,
      loginType: 1
    })
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data))
    showToast('登录成功')
    setTimeout(() => {
      router.replace('/home')
    }, 500)
  } catch (error) {
    showToast(error.message || '登录失败')
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #1E293B 0%, #0F172A 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-box {
  background: white;
  border-radius: var(--radius-xl);
  padding: 40px 32px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 26px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 16px 0 8px;
}

.login-header p {
  color: var(--text-muted);
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 32px;
}

.login-form .van-field {
  margin-bottom: 16px;
  background: #F8FAFC;
  border-radius: var(--radius-sm);
  padding: 12px 16px;
}

.form-actions {
  margin-top: 32px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.login-btn {
  background: var(--primary-gradient) !important;
  border: none !important;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--radius-sm) !important;
  box-shadow: 0 4px 14px rgba(79, 110, 247, 0.4);
  transition: all 0.2s;
}

.login-btn:active {
  opacity: 0.9;
  transform: scale(0.98);
}

.register-link {
  text-align: center;
  color: var(--primary);
  text-decoration: none;
  font-size: 14px;
}

.login-tips {
  text-align: center;
  padding: 12px;
  background: #F8FAFC;
  border-radius: var(--radius-sm);
  font-size: 12px;
  color: var(--text-secondary);
}

.login-tips p {
  margin: 0;
}
</style>