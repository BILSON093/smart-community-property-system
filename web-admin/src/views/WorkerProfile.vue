<template>
  <el-card>
    <template #header>
      <span>个人中心</span>
    </template>

    <el-form :model="form" label-width="100px" style="max-width: 500px">
      <el-form-item label="头像">
        <el-avatar :size="100" :src="form.avatar">
          <el-icon><User /></el-icon>
        </el-avatar>
      </el-form-item>
      <el-form-item label="账号">
        <el-input v-model="form.username" placeholder="请输入账号" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="form.realName" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="旧密码">
        <el-input v-model="form.oldPassword" type="password" placeholder="不修改请留空" />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" type="password" placeholder="不修改请留空" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const form = ref({
  username: '',
  realName: '',
  phone: '',
  avatar: '',
  oldPassword: '',
  newPassword: ''
})

onMounted(() => {
  loadUserInfo()
})

const loadUserInfo = async () => {
  try {
    // 先从localStorage获取基本信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    form.value.username = userInfo.username || ''
    form.value.realName = userInfo.realName || ''
    form.value.phone = userInfo.phone || ''
    form.value.avatar = userInfo.avatar || ''
    
    // 然后调用后端接口获取最新信息
    const res = await request.get('/user/info')
    if (res.code === 200 || res.data) {
      const data = res.data || {}
      form.value.username = data.username || form.value.username
      form.value.realName = data.realName || form.value.realName
      form.value.phone = data.phone || form.value.phone
      form.value.avatar = data.avatar || form.value.avatar
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const handleSave = async () => {
  try {
    await request.put('/user/update', form.value)
    ElMessage.success('保存成功')
    
    // 更新localStorage中的信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    userInfo.username = form.value.username
    userInfo.realName = form.value.realName
    userInfo.phone = form.value.phone
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  } catch (error) {
    ElMessage.error(error.message || '保存失败')
  }
}
</script>
