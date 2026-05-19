<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card class="stat-card">
          <el-statistic title="已完成工单" :value="workerStats.completedCount">
            <template #suffix>单</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <el-statistic title="平均评分" :value="workerStats.avgScore" :precision="1">
            <template #suffix>分</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <el-statistic title="平均耗时" :value="workerStats.avgMinutes" :precision="0">
            <template #suffix>分钟</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>我的工单</span>
      </template>

      <el-table :data="repairList" border stripe>
      <el-table-column prop="id" label="工单号" width="100" />
      <el-table-column prop="content" label="故障描述" width="300" />
      <el-table-column label="图片" width="200">
        <template #default="{ row }">
          <el-image
            v-if="row.images"
            style="width: 50px; height: 50px"
            :src="getImage(row.images)"
            :preview-src-list="getImages(row.images)"
            fit="cover"
          />
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 1"
            type="primary"
            size="small"
            @click="startRepair(row.id)"
          >
            开始维修
          </el-button>
          <el-button
            v-if="row.status === 2"
            type="success"
            size="small"
            @click="completeRepair(row.id)"
          >
            完成维修
          </el-button>
          <el-button
            v-if="row.status === 1 || row.status === 2"
            type="danger"
            size="small"
            @click="handleCancelRepair(row.id)"
          >
            退单
          </el-button>
        </template>
      </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const repairList = ref([])
const workerStats = ref({ completedCount: 0, avgScore: 0, avgMinutes: 0 })

onMounted(() => {
  loadRepairs()
  loadWorkerStats()
})

const loadWorkerStats = async () => {
  try {
    const res = await request.get('/repair/worker/stats')
    if (res.code === 200 && res.data) {
      workerStats.value = res.data
    }
  } catch (e) {
    // ignore
  }
}

const loadRepairs = async () => {
  const res = await request.get('/repair/worker', { params: { page: 1, size: 100 } })
  repairList.value = res.data.records || []
}

const getImage = (images) => {
  if (!images) return ''
  const arr = JSON.parse(images)
  return arr[0]
}

const getImages = (images) => {
  if (!images) return []
  return JSON.parse(images)
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'info', 2: 'primary', 3: 'success' }
  return map[status]
}

const getStatusText = (status) => {
  const map = { 0: '待派单', 1: '已派单', 2: '维修中', 3: '已完成' }
  return map[status]
}

const startRepair = async (id) => {
  await request.post(`/repair/start/${id}`)
  ElMessage.success('开始维修')
  loadRepairs()
}

const completeRepair = async (id) => {
  await request.post(`/repair/complete/${id}`)
  ElMessage.success('维修完成')
  loadRepairs()
}

const handleCancelRepair = async (id) => {
  try {
    await request.post(`/repair/cancel/${id}`)
    ElMessage.success('退单成功')
    loadRepairs()
  } catch (error) {
    console.error('退单失败:', error)
    ElMessage.error('退单失败')
  }
}
</script>
