<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="业主总数" :value="stats.ownerCount">
            <template #suffix>人</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="待处理报修" :value="stats.pendingRepairs">
            <template #suffix>单</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="本月缴费" :value="stats.monthFee" :precision="2">
            <template #prefix>¥</template>
          </el-statistic>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <el-statistic title="今日活跃" :value="stats.todayActive">
            <template #suffix>人</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近七日缴费趋势</span>
            </div>
          </template>
          <div ref="feeChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近七日论坛发帖趋势</span>
            </div>
          </template>
          <div ref="forumChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const feeChart = ref()
const forumChart = ref()

const stats = ref({
  ownerCount: 0,
  pendingRepairs: 0,
  monthFee: 0,
  todayActive: 0
})

const weekFeeTrend = ref({})
const weekForumTrend = ref({})

onMounted(async () => {
  await loadStatistics()
  renderCharts()
})

const loadStatistics = async () => {
  try {
    const res = await request.get('/admin/statistics')
    if (res.code === 200 && res.data) {
      stats.value = {
        ownerCount: res.data.ownerCount || 0,
        pendingRepairs: res.data.pendingRepairs || 0,
        monthFee: res.data.monthFee || 0,
        todayActive: res.data.todayActive || 0
      }
      weekFeeTrend.value = res.data.weekFeeTrend || {}
      weekForumTrend.value = res.data.weekForumTrend || {}
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

const renderCharts = () => {
  // 折线图 - 近七日缴费趋势
  const feeLine = echarts.init(feeChart.value)
  const feeDates = Object.keys(weekFeeTrend.value).map(date => {
    const d = new Date(date)
    return `${d.getMonth() + 1}/${d.getDate()}`
  })
  const feeAmounts = Object.values(weekFeeTrend.value)
  feeLine.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: feeDates
    },
    yAxis: { type: 'value' },
    series: [
      {
        data: feeAmounts,
        type: 'line',
        smooth: true,
        itemStyle: { color: '#409EFF' },
        lineStyle: { color: '#409EFF' }
      }
    ]
  })

  // 折线图 - 近七日论坛发帖趋势
  const forumLine = echarts.init(forumChart.value)
  const forumDates = Object.keys(weekForumTrend.value).map(date => {
    const d = new Date(date)
    return `${d.getMonth() + 1}/${d.getDate()}`
  })
  const forumCounts = Object.values(weekForumTrend.value)
  forumLine.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: forumDates
    },
    yAxis: { 
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        data: forumCounts,
        type: 'line',
        smooth: true,
        itemStyle: { color: '#67C23A' },
        lineStyle: { color: '#67C23A' }
      }
    ]
  })

  window.addEventListener('resize', () => {
    feeLine.resize()
    forumLine.resize()
  })
}
</script>

<style scoped>
.stat-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
</style>
