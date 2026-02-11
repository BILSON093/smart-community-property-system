<template>
  <el-card>
    <template #header>
      <span>已完成订单</span>
    </template>

    <el-table :data="completedList" border stripe>
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
      <el-table-column label="完成时间" width="180">
        <template #default="{ row }">
          {{ row.completeTime || '未完成' }}
        </template>
      </el-table-column>
      <el-table-column label="评分" width="100">
        <template #default="{ row }">
          <span v-if="row.evaluation">
            {{ row.evaluation.score }}星
          </span>
          <span v-else>
            未评价
          </span>
        </template>
      </el-table-column>
      <el-table-column label="评价" width="200">
        <template #default="{ row }">
          <span v-if="row.evaluation">
            {{ row.evaluation.comment }}
          </span>
          <span v-else>
            未评价
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="info" size="small" @click="viewDetail(row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="订单详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="业主">{{ currentRepair.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRepair.phone }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentRepair.content }}</el-descriptions-item>
        <el-descriptions-item label="维修员">{{ currentRepair.workerName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="success">已完成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ currentRepair.createTime }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ currentRepair.completeTime || '未完成' }}</el-descriptions-item>
        <el-descriptions-item label="评分" v-if="currentRepair.evaluation">
          {{ currentRepair.evaluation.score }}星
        </el-descriptions-item>
        <el-descriptions-item label="评价" v-if="currentRepair.evaluation" :span="2">
          {{ currentRepair.evaluation.comment }}
        </el-descriptions-item>
        <el-descriptions-item label="报修图片" :span="2">
          <div v-if="currentRepair.images">
            <el-image
              v-for="(img, index) in getImages(currentRepair.images)"
              :key="index"
              :src="img"
              style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px"
              :preview-src-list="getImages(currentRepair.images)"
              fit="cover"
            />
          </div>
          <span v-else>无图片</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const completedList = ref([])
const showDetailDialog = ref(false)
const currentRepair = ref({})

onMounted(() => {
  loadCompletedRepairs()
})

const loadCompletedRepairs = async () => {
  const res = await request.get('/repair/worker/completed', { params: { page: 1, size: 100 } })
  completedList.value = res.data.records || []
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

const viewDetail = (row) => {
  currentRepair.value = { ...row }
  showDetailDialog.value = true
}
</script>

<style scoped>
</style>