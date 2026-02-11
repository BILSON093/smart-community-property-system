<template>
  <div class="activity">
    <van-nav-bar title="社区活动" />
    
    <van-list v-model:loading="loading" :finished="finished" @load="onLoad">
      <van-cell v-for="item in activityList" :key="item.id" center>
        <template #title>
          <div class="activity-card" @click="showDetail(item)">
            <img :src="item.coverImage" />
            <div class="info">
              <h3>{{ item.title }}</h3>
              <p class="location">
                <van-icon name="location-o" />
                {{ item.location }}
              </p>
              <p class="time">
                <van-icon name="clock-o" />
                {{ item.formattedStartTime }}
              </p>
              <p class="description">{{ item.description }}</p>
            </div>
          </div>
        </template>
      </van-cell>
    </van-list>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const activityList = ref([])
const loading = ref(false)
const finished = ref(false)

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(async () => {
  const res = await request.get('/activity/list', { params: { page: 1, size: 100 } })
  const activities = res.data.records || []
  activityList.value = activities.map(item => ({
    ...item,
    formattedStartTime: formatDate(item.startTime)
  }))
  finished.value = true
})

const onLoad = () => {}

const showDetail = (item) => {
  router.push(`/activity/${item.id}`)
}
</script>

<style scoped>
.activity {
  min-height: 100vh;
  background: #f5f5f5;
}

.activity-card {
  display: flex;
  gap: 15px;
  padding: 10px 0;
}

.activity-card img {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  object-fit: cover;
}

.info {
  flex: 1;
}

.info h3 {
  font-size: 15px;
  margin-bottom: 8px;
}

.info p {
  font-size: 13px;
  color: #999;
  margin-bottom: 5px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.description {
  font-size: 12px;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
