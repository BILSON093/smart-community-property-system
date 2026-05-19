<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>通知管理</span>
          </div>
        </div>
      </template>

      <el-table :data="notifications" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="接收用户ID" width="120" />
        <el-table-column prop="title" label="标题" min-width="150" />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRead" label="已读状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isRead === 1 ? 'success' : 'danger'" size="small">
              {{ row.isRead === 1 ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @current-change="loadNotifications"
        @size-change="loadNotifications"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const notifications = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadNotifications = async () => {
  loading.value = true
  try {
    const res = await request.get('/notification/list', {
      params: { page: page.value, size: pageSize.value }
    })
    if (res.code === 200) {
      notifications.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const getTypeTag = (type) => {
  const map = { repair: 'warning', fee: 'danger', system: 'info' }
  return map[type] || 'info'
}

const getTypeLabel = (type) => {
  const map = { repair: '报修', fee: '缴费', system: '系统' }
  return map[type] || type || '系统'
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
</style>
