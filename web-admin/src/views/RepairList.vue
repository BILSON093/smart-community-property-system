<template>
  <div>
    <div style="margin-bottom: 16px; display: flex; gap: 12px; align-items: center;">
      <el-select v-model="selectedType" placeholder="报修分类" clearable style="width: 150px" @change="loadRepairs">
        <el-option label="全部分类" :value="null" />
        <el-option label="水电维修" value="水电维修" />
        <el-option label="家电维修" value="家电维修" />
        <el-option label="管道疏通" value="管道疏通" />
        <el-option label="门窗维修" value="门窗维修" />
        <el-option label="其他" value="其他" />
      </el-select>
    </div>

    <el-table :data="repairList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="ownerName" label="业主" width="120" />
      <el-table-column label="住址" width="200">
        <template #default="{ row }">
          {{ row.building }} {{ row.unit }} {{ row.room }}
        </template>
      </el-table-column>
      <el-table-column prop="type" label="分类" width="100" />
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
      <el-table-column prop="createTime" label="提交时间" width="180" />
      <el-table-column label="操作" width="300">
        <template #default="{ row }">
          <el-button v-if="parseInt(status) === 0" type="primary" size="small" @click="handleDispatch(row)">派单</el-button>
          <el-button type="info" size="small" @click="handleView(row)">查看详情</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 派单对话框 -->
    <el-dialog v-model="showDispatchDialog" title="派单" width="400px">
      <el-form label-width="80px">
        <el-form-item label="维修员">
          <el-select v-model="selectedWorkerId" placeholder="请选择维修员" style="width: 100%">
            <el-option
              v-for="worker in workerList"
              :key="worker.userId"
              :label="worker.realName"
              :value="worker.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDispatchDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmDispatch">确认派单</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="报修详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="业主">{{ currentRepair.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentRepair.phone }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentRepair.content }}</el-descriptions-item>
        <el-descriptions-item label="维修员">{{ currentRepair.workerName || '未派单' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentRepair.status === 0" type="warning">待派单</el-tag>
          <el-tag v-else-if="currentRepair.status === 1" type="primary">已派单</el-tag>
          <el-tag v-else-if="currentRepair.status === 2" type="success">维修中</el-tag>
          <el-tag v-else type="info">已完成</el-tag>
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch, defineProps } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const props = defineProps({
  status: Number
})

const repairList = ref([])
const workerList = ref([])
const showDispatchDialog = ref(false)
const showDetailDialog = ref(false)
const selectedWorkerId = ref(null)
const currentRepairId = ref(null)
const currentRepair = ref({})
const selectedType = ref(null)

onMounted(() => {
  loadRepairs()
  loadWorkers()
})

// 监听status变化，重新加载数据
watch(() => props.status, () => {
  loadRepairs()
})

const loadRepairs = async () => {
  const statusValue = props.status !== null ? parseInt(props.status) : null
  const params = { status: statusValue, page: 1, size: 100 }
  if (selectedType.value) params.type = selectedType.value
  const res = await request.get('/repair/list', { params })
  repairList.value = res.data.records || []
}

const loadWorkers = async () => {
  const res = await request.get('/admin/workers')
  if (res.data) {
    workerList.value = res.data
      .filter(w => w.status === 1) // 只显示已审核通过的维修员
      .map(w => ({
        userId: w.id || w.userId,
        realName: w.realName
      }))
  } else {
    workerList.value = []
  }
}

const getImage = (images) => {
  if (!images) return ''
  try {
    const arr = JSON.parse(images)
    return arr[0]
  } catch (e) {
    return ''
  }
}

const getImages = (images) => {
  if (!images) return []
  try {
    return JSON.parse(images)
  } catch (e) {
    return []
  }
}

const handleDispatch = (row) => {
  currentRepairId.value = row.id
  selectedWorkerId.value = null
  showDispatchDialog.value = true
}

const confirmDispatch = async () => {
  if (!selectedWorkerId.value) {
    ElMessage.warning('请选择维修员')
    return
  }
  console.log('派单参数:', { repairId: currentRepairId.value, workerId: selectedWorkerId.value })
  await request.post('/repair/dispatch', { repairId: currentRepairId.value, workerId: selectedWorkerId.value })
  ElMessage.success('派单成功')
  showDispatchDialog.value = false
  loadRepairs()
}

const handleView = (row) => {
  currentRepair.value = { ...row }
  showDetailDialog.value = true
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/repair/${id}`)
    ElMessage.success('删除成功')
    loadRepairs()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}
</script>
