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

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>维修分类统计</span>
            </div>
          </template>
          <div ref="repairTypeChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>缴费收缴率</span>
            </div>
          </template>
          <div ref="feeRateChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <el-statistic title="维修员总数" :value="stats.workerCount">
            <template #suffix>人</template>
          </el-statistic>
        </el-card>
        <el-card class="stat-card" style="margin-top: 20px">
          <el-statistic title="维修员平均评分" :value="stats.avgWorkerScore" :precision="1">
            <template #suffix>分</template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const feeChart = ref()
const forumChart = ref()
const repairTypeChart = ref()
const feeRateChart = ref()

const stats = ref({
  ownerCount: 0,
  pendingRepairs: 0,
  monthFee: 0,
  todayActive: 0,
  workerCount: 0,
  avgWorkerScore: 0,
  repairTypes: {},
  feeRate: { paid: 0, unpaid: 0 }
})

const weekFeeTrend = ref({})
const weekForumTrend = ref({})
let feeChartInstance = null
let forumChartInstance = null
let repairTypeChartInstance = null
let feeRateChartInstance = null
let resizeHandler = null

onMounted(async () => {
  await loadStatistics()
  nextTick(() => {
    renderCharts()
  })
})

onBeforeUnmount(() => {
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
    resizeHandler = null
  }
  if (feeChartInstance) { feeChartInstance.dispose(); feeChartInstance = null }
  if (forumChartInstance) { forumChartInstance.dispose(); forumChartInstance = null }
  if (repairTypeChartInstance) { repairTypeChartInstance.dispose(); repairTypeChartInstance = null }
  if (feeRateChartInstance) { feeRateChartInstance.dispose(); feeRateChartInstance = null }
})

const loadStatistics = async () => {
  try {
    const res = await request.get('/admin/statistics')
    if (res.code === 200 && res.data) {
      stats.value = {
        ownerCount: res.data.ownerCount || 0,
        pendingRepairs: res.data.pendingRepairs || 0,
        monthFee: res.data.monthFee || 0,
        todayActive: res.data.todayActive || 0,
        workerCount: res.data.workerCount || 0,
        avgWorkerScore: res.data.avgWorkerScore || 0,
        repairTypes: res.data.repairTypes || {},
        feeRate: res.data.feeRate || { paid: 0, unpaid: 0 }
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
  feeChartInstance = echarts.init(feeChart.value)
  const feeLine = feeChartInstance
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
  forumChartInstance = echarts.init(forumChart.value)
  const forumLine = forumChartInstance
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

  // 维修分类饼图
  repairTypeChartInstance = echarts.init(repairTypeChart.value)
  const repairTypeData = Object.entries(stats.value.repairTypes).map(([name, value]) => ({ name, value }))
  repairTypeChartInstance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      data: repairTypeData.length > 0 ? repairTypeData : [{ name: '暂无数据', value: 0 }],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
    }]
  })

  // 缴费收缴率柱状图
  feeRateChartInstance = echarts.init(feeRateChart.value)
  feeRateChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['已缴费', '未缴费'] },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{
      type: 'bar',
      data: [
        { value: stats.value.feeRate.paid, itemStyle: { color: '#67C23A' } },
        { value: stats.value.feeRate.unpaid, itemStyle: { color: '#F56C6C' } }
      ],
      barWidth: '40%'
    }]
  })

  resizeHandler = () => {
    feeLine.resize()
    forumLine.resize()
    repairTypeChartInstance?.resize()
    feeRateChartInstance?.resize()
  }
  window.addEventListener('resize', resizeHandler)
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
