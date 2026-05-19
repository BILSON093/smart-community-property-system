<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon stat-icon-blue">
              <el-icon :size="24"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">业主总数</div>
              <div class="stat-value">{{ stats.ownerCount }}<span class="stat-unit">人</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon stat-icon-orange">
              <el-icon :size="24"><DocumentChecked /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">待处理报修</div>
              <div class="stat-value">{{ stats.pendingRepairs }}<span class="stat-unit">单</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon stat-icon-green">
              <el-icon :size="24"><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">本月缴费</div>
              <div class="stat-value"><span class="stat-prefix">¥</span>{{ stats.monthFee.toFixed(2) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon stat-icon-purple">
              <el-icon :size="24"><DataLine /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">今日活跃</div>
              <div class="stat-value">{{ stats.todayActive }}<span class="stat-unit">人</span></div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <div class="title-bar"></div>
                <span>近七日缴费趋势</span>
              </div>
            </div>
          </template>
          <div ref="feeChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <div class="title-bar"></div>
                <span>近七日论坛发帖趋势</span>
              </div>
            </div>
          </template>
          <div ref="forumChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <div class="title-bar"></div>
                <span>维修分类统计</span>
              </div>
            </div>
          </template>
          <div ref="repairTypeChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <div class="card-title">
                <div class="title-bar"></div>
                <span>缴费收缴率</span>
              </div>
            </div>
          </template>
          <div ref="feeRateChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-card-inner">
            <div class="stat-icon stat-icon-cyan">
              <el-icon :size="24"><Tools /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">维修员总数</div>
              <div class="stat-value">{{ stats.workerCount }}<span class="stat-unit">人</span></div>
            </div>
          </div>
        </el-card>
        <el-card class="stat-card" style="margin-top: 20px">
          <div class="stat-card-inner">
            <div class="stat-icon stat-icon-yellow">
              <el-icon :size="24"><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">维修员平均评分</div>
              <div class="stat-value">{{ stats.avgWorkerScore.toFixed(1) }}<span class="stat-unit">分</span></div>
            </div>
          </div>
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
      data: feeDates,
      axisLine: { lineStyle: { color: '#E2E8F0' } },
      axisLabel: { color: '#94A3B8' }
    },
    yAxis: {
      type: 'value',
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#F1F5F9' } },
      axisLabel: { color: '#94A3B8' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [
      {
        data: feeAmounts,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: { color: '#4F6EF7' },
        lineStyle: { color: '#4F6EF7', width: 3 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(79, 110, 247, 0.25)' },
            { offset: 1, color: 'rgba(79, 110, 247, 0.02)' }
          ])
        }
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
      data: forumDates,
      axisLine: { lineStyle: { color: '#E2E8F0' } },
      axisLabel: { color: '#94A3B8' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#F1F5F9' } },
      axisLabel: { color: '#94A3B8' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [
      {
        data: forumCounts,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        itemStyle: { color: '#10B981' },
        lineStyle: { color: '#10B981', width: 3 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16, 185, 129, 0.25)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0.02)' }
          ])
        }
      }
    ]
  })

  // 维修分类饼图
  repairTypeChartInstance = echarts.init(repairTypeChart.value)
  const repairTypeData = Object.entries(stats.value.repairTypes).map(([name, value]) => ({ name, value }))
  repairTypeChartInstance.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle', textStyle: { color: '#64748B' } },
    color: ['#4F6EF7', '#10B981', '#F59E0B', '#EF4444', '#6366F1', '#EC4899'],
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      data: repairTypeData.length > 0 ? repairTypeData : [{ name: '暂无数据', value: 0 }],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } },
      label: { color: '#64748B' }
    }]
  })

  // 缴费收缴率柱状图
  feeRateChartInstance = echarts.init(feeRateChart.value)
  feeRateChartInstance.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: ['已缴费', '未缴费'],
      axisLine: { lineStyle: { color: '#E2E8F0' } },
      axisLabel: { color: '#94A3B8' }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      axisLine: { show: false },
      splitLine: { lineStyle: { color: '#F1F5F9' } },
      axisLabel: { color: '#94A3B8' }
    },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    series: [{
      type: 'bar',
      data: [
        { value: stats.value.feeRate.paid, itemStyle: { color: '#10B981', borderRadius: [6, 6, 0, 0] } },
        { value: stats.value.feeRate.unpaid, itemStyle: { color: '#EF4444', borderRadius: [6, 6, 0, 0] } }
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
.dashboard {
  min-height: 100%;
}

.stat-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover) !important;
}

.stat-card-inner {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-icon-blue {
  background: linear-gradient(135deg, #4F6EF7 0%, #6B85F9 100%);
}

.stat-icon-orange {
  background: linear-gradient(135deg, #F59E0B 0%, #FBBF24 100%);
}

.stat-icon-green {
  background: linear-gradient(135deg, #10B981 0%, #34D399 100%);
}

.stat-icon-purple {
  background: linear-gradient(135deg, #6366F1 0%, #818CF8 100%);
}

.stat-icon-cyan {
  background: linear-gradient(135deg, #06B6D4 0%, #22D3EE 100%);
}

.stat-icon-yellow {
  background: linear-gradient(135deg, #EAB308 0%, #FDE047 100%);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
}

.stat-unit {
  font-size: 14px;
  font-weight: 400;
  color: var(--text-muted);
  margin-left: 4px;
}

.stat-prefix {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-secondary);
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-hover) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  color: var(--text-primary);
}

.title-bar {
  width: 4px;
  height: 18px;
  background: var(--primary-gradient);
  border-radius: 2px;
}

.chart-container {
  height: 300px;
}
</style>
