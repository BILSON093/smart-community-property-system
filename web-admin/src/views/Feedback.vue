<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <span>留言反馈管理</span>
      </div>
    </template>

    <!-- 筛选条件 -->
    <div style="margin-bottom: 20px; display: flex; gap: 10px; flex-wrap: wrap">
      <el-select
        v-model="filterData.status"
        placeholder="状态"
        style="width: 120px"
        @change="loadFeedbacks"
      >
        <el-option label="全部" value="" />
        <el-option label="待处理" value="0" />
        <el-option label="已处理" value="1" />
      </el-select>
      
      <el-button @click="resetFilter">重置筛选</el-button>
    </div>

    <el-table :data="feedbackList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="userName" label="业主姓名" width="120" />
      <el-table-column label="住址" width="200">
        <template #default="{ row }">
          {{ row.building }} {{ row.unit }} {{ row.room }}
        </template>
      </el-table-column>
      <el-table-column prop="content" label="反馈内容" width="200">
        <template #default="{ row }">
          <el-tooltip :content="row.content" placement="top">
            <span>{{ row.content.length > 30 ? row.content.substring(0, 30) + '...' : row.content }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="reply" label="回复内容" width="200">
        <template #default="{ row }">
          <el-tooltip :content="row.reply || '未回复'" placement="top">
            <span>{{ row.reply ? (row.reply.length > 30 ? row.reply.substring(0, 30) + '...' : row.reply) : '未回复' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'warning'">
            {{ row.status === 1 ? '已处理' : '待处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleReply(row)">回复</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 回复对话框 -->
    <el-dialog v-model="showReplyDialog" title="回复留言" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="业主姓名">
          <el-input v-model="formData.userName" disabled />
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="formData.address" disabled />
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input v-model="formData.content" type="textarea" :rows="5" placeholder="反馈内容" disabled />
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input v-model="formData.reply" type="textarea" :rows="5" placeholder="请输入回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReplyDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">回复</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const feedbackList = ref([])
const showReplyDialog = ref(false)
const formData = ref({
  id: null,
  content: '',
  reply: '',
  status: 0,
  userName: '',
  address: ''
})

const filterData = ref({
  status: ''
})

onMounted(() => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  loadFeedbacks()
})

const loadFeedbacks = async () => {
  try {
    const params = {
      page: 1,
      size: 100
    }
    if (filterData.value.status !== '') {
      params.status = filterData.value.status
    }
    const res = await request.get('/feedback/list', { params })
    
    if (res.data && res.data.records) {
      const feedbacks = res.data.records
      
      const userIds = [...new Set(feedbacks.map(f => f.userId))]
      
      if (userIds.length > 0) {
        const ownerRes = await request.get('/admin/owners')
        if (ownerRes.data) {
          const ownerMap = {}
          ownerRes.data.forEach(owner => {
            ownerMap[owner.userId || owner.id] = {
              building: owner.building || '',
              unit: owner.unit || '',
              room: owner.room || ''
            }
          })
          
          feedbacks.forEach(feedback => {
            const ownerInfo = ownerMap[feedback.userId]
            if (ownerInfo) {
              feedback.building = ownerInfo.building
              feedback.unit = ownerInfo.unit
              feedback.room = ownerInfo.room
            } else {
              feedback.building = ''
              feedback.unit = ''
              feedback.room = ''
            }
          })
        }
      }
      
      feedbackList.value = feedbacks
    }
  } catch (error) {
    console.error('加载反馈列表失败:', error)
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    }
  }
}

const resetFilter = () => {
  filterData.value = {
    status: '',
    keyword: ''
  }
  loadFeedbacks()
}

const handleReply = (row) => {
  formData.value = {
    id: row.id,
    content: row.content,
    reply: row.reply || '',
    status: row.status,
    userName: row.userName,
    address: `${row.building || ''} ${row.unit || ''} ${row.room || ''}`.trim()
  }
  showReplyDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.reply) {
    ElMessage.warning('请输入回复内容')
    return
  }

  try {
    const feedbackData = {
      id: formData.value.id,
      reply: formData.value.reply,
      status: 1
    }
    console.log('回复留言数据:', feedbackData)
    await request.put('/feedback/update', feedbackData)
    ElMessage.success('回复成功')
    showReplyDialog.value = false
    loadFeedbacks()
  } catch (error) {
    console.error('回复留言失败:', error)
    ElMessage.error('回复失败，请重试')
  }
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该留言吗?', '提示', { type: 'warning' })
  await request.delete(`/feedback/${id}`)
  ElMessage.success('删除成功')
  loadFeedbacks()
}
</script>

<style scoped>

</style>