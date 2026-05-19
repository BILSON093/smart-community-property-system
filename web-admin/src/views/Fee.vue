<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center">
        <div style="display: flex; align-items: center; gap: 10px">
          <span>缴费管理</span>
          <el-button 
            type="info" 
            size="small" 
            @click="showSettingsDialog = true"
            icon="Setting"
          >
            设置
          </el-button>
        </div>
        <div>
          <el-button type="warning" @click="handleUrgePayment" icon="Bell">一键催缴</el-button>
          <el-button type="primary" @click="showAddDialog = true">新增缴费</el-button>
          <el-button type="success" @click="showBatchAddDialog = true">批量新增</el-button>
        </div>
      </div>
    </template>

    <!-- 筛选条件 -->
    <div style="margin-bottom: 20px; display: flex; gap: 10px; flex-wrap: wrap">
      <!-- 按月份筛选 -->
      <el-date-picker
        v-model="filterData.month"
        type="month"
        placeholder="选择月份"
        format="YYYY-MM"
        value-format="YYYY-MM"
        style="width: 150px"
        @change="loadFees"
      />
      
      <!-- 按缴费状态筛选 -->
      <el-select
        v-model="filterData.status"
        placeholder="缴费状态"
        style="width: 120px"
        @change="loadFees"
      >
        <el-option label="全部" value="" />
        <el-option label="未缴" value="0" />
        <el-option label="已缴" value="1" />
      </el-select>
      
      <!-- 按费用类型筛选 -->
      <el-select
        v-model="filterData.type"
        placeholder="费用类型"
        style="width: 120px"
        @change="loadFees"
      >
        <el-option label="全部" value="" />
        <el-option label="物业费" value="物业费" />
        <el-option label="水费" value="水费" />
        <el-option label="电费" value="电费" />
        <el-option label="燃气费" value="燃气费" />
        <el-option label="其他" value="其他" />
      </el-select>
      
      <!-- 搜索框 -->
      <el-input
        v-model="filterData.keyword"
        placeholder="搜索业主姓名、住址等信息"
        style="width: 280px"
        @keyup.enter="loadFees"
      >
        <template #append>
          <el-button @click="loadFees">搜索</el-button>
        </template>
      </el-input>
      
      <!-- 重置按钮 -->
      <el-button @click="resetFilter">重置筛选</el-button>
    </div>

    <el-table :data="feeList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="ownerName" label="业主" width="120" />
      <el-table-column label="住址" width="200">
        <template #default="{ row }">
          {{ row.building }} {{ row.unit }} {{ row.room }}
        </template>
      </el-table-column>
      <el-table-column prop="type" label="费用类型" width="100" />
      <el-table-column prop="month" label="月份" width="100" />
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="{ row }">
          ¥{{ row.amount }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'warning' : 'success'">
            {{ row.status === 0 ? '未缴' : '已缴' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payTime" label="缴费时间" width="180" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增对话框 -->
    <el-dialog v-model="showAddDialog" title="新增缴费" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="业主">
          <el-select v-model="formData.ownerId" placeholder="请选择业主">
            <el-option
              v-for="owner in ownerList"
              :key="owner.userId"
              :label="owner.realName"
              :value="owner.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型">
          <el-select v-model="formData.type" placeholder="请选择费用类型" @change="handleTypeChange">
            <el-option label="物业费" value="物业费" />
            <el-option label="水费" value="水费" />
            <el-option label="电费" value="电费" />
            <el-option label="燃气费" value="燃气费" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="月份">
          <el-date-picker
            v-model="formData.month"
            type="month"
            placeholder="选择月份"
            format="YYYY-MM"
            value-format="YYYY-MM"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item v-if="formData.type !== '其他'" :label="getUsageLabel(formData.type)">
          <el-input-number v-model="formData.usage" :min="0" :precision="2" :step="0.01" style="width: 100%" @change="calculateAmount" />
        </el-form-item>
        <el-form-item label="金额">
          <el-input-number v-model="formData.amount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" placeholder="请输入备注信息（可选）" rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="0">未缴</el-radio>
            <el-radio :value="1">已缴</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确认</el-button>
      </template>
    </el-dialog>

    <!-- 批量新增对话框 -->
    <el-dialog v-model="showBatchAddDialog" title="批量新增缴费" width="800px">
      <div style="display: flex; flex-direction: column; gap: 20px">
        <!-- 搜索业主 -->
        <div>
          <el-input
            v-model="searchKeyword"
            placeholder="请输入业主姓名、楼栋、单元或房间号"
            style="width: 300px; margin-right: 10px"
          />
          <el-button type="primary" @click="searchOwners">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </div>

        <!-- 业主列表 -->
        <el-table
          :data="searchedOwners"
          border
          stripe
          style="width: 100%"
          max-height="300px"
          @selection-change="handleSelectionChange"
          row-key="userId"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="realName" label="业主姓名" width="120" />
          <el-table-column prop="building" label="楼栋" width="80" />
          <el-table-column prop="unit" label="单元" width="80" />
          <el-table-column prop="room" label="房间号" width="100" />
          <el-table-column prop="phone" label="联系电话" width="150" />
        </el-table>

        <!-- 缴费信息 -->
        <el-form :model="batchFormData" label-width="100px">
          <el-form-item label="费用类型">
            <el-select v-model="batchFormData.type" placeholder="请选择费用类型" @change="handleBatchTypeChange">
              <el-option label="物业费" value="物业费" />
              <el-option label="水费" value="水费" />
              <el-option label="电费" value="电费" />
              <el-option label="燃气费" value="燃气费" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="月份">
            <el-date-picker
              v-model="batchFormData.month"
              type="month"
              placeholder="选择月份"
              format="YYYY-MM"
              value-format="YYYY-MM"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item v-if="batchFormData.type !== '其他'" :label="getUsageLabel(batchFormData.type)">
            <el-input-number v-model="batchFormData.usage" :min="0" :precision="2" :step="0.01" style="width: 100%" @change="calculateBatchAmount" />
          </el-form-item>
          <el-form-item label="金额">
            <el-input-number v-model="batchFormData.amount" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="batchFormData.remark" type="textarea" placeholder="请输入备注信息（可选）" rows="3" />
          </el-form-item>
          <el-form-item label="状态">
            <el-radio-group v-model="batchFormData.status">
              <el-radio :value="0">未缴</el-radio>
              <el-radio :value="1">已缴</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showBatchAddDialog = false">取消</el-button>
        <el-button type="success" @click="handleBatchAdd">批量添加</el-button>
      </template>
    </el-dialog>

    <!-- 设置对话框 -->
    <el-dialog v-model="showSettingsDialog" title="费用单价设置" width="600px">
      <el-form :model="settingsData" label-width="180px">
        <el-form-item label="物业费单价（元/㎡）">
          <el-input-number v-model="settingsData.propertyFee" :min="0" :precision="2" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="水费单价（元/吨）">
          <el-input-number v-model="settingsData.waterFee" :min="0" :precision="2" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="电费单价（元/度）">
          <el-input-number v-model="settingsData.electricityFee" :min="0" :precision="2" :step="0.01" style="width: 100%" />
        </el-form-item>
        <el-form-item label="燃气费单价（元/立方）">
          <el-input-number v-model="settingsData.gasFee" :min="0" :precision="2" :step="0.01" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSettingsDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSettings">保存设置</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const feeList = ref([])
const ownerList = ref([])
const showAddDialog = ref(false)
const formData = ref({
  ownerId: null,
  type: '物业费',
  month: '',
  amount: 0,
  status: 0,
  usage: 0,
  remark: ''
})

// 筛选数据
const filterData = ref({
  month: '',
  status: '',
  type: '',
  keyword: ''
})

// 批量新增相关变量
const showBatchAddDialog = ref(false)
const searchKeyword = ref('')
const searchedOwners = ref([])
const selectedOwners = ref([])
const batchFormData = ref({
  type: '物业费',
  month: '',
  amount: 0,
  status: 0,
  usage: 0,
  remark: ''
})

// 设置相关变量
const showSettingsDialog = ref(false)
const settingsData = ref({
  propertyFee: 2.5,
  waterFee: 3.8,
  electricityFee: 0.6,
  gasFee: 2.2
})

// 保存设置
const handleSaveSettings = async () => {
  try {
    await request.post('/fee/settings', settingsData.value)
    ElMessage.success('设置保存成功')
    showSettingsDialog.value = false
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败，请重试')
  }
}

// 加载设置数据
const loadSettings = async () => {
  try {
    const res = await request.get('/fee/settings')
    if (res.data) {
      settingsData.value = res.data
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

// 获取用量标签
const getUsageLabel = (type) => {
  switch (type) {
    case '物业费': return '面积（㎡）'
    case '水费': return '用水量（吨）'
    case '电费': return '用电量（度）'
    case '燃气费': return '用气量（立方）'
    default: return '用量'
  }
}

// 处理费用类型变更
const handleTypeChange = () => {
  formData.value.usage = 0
  formData.value.amount = 0
}

// 计算金额
const calculateAmount = () => {
  if (!formData.value.usage) {
    formData.value.amount = 0
    return
  }
  
  let unitPrice = 0
  switch (formData.value.type) {
    case '物业费': unitPrice = settingsData.value.propertyFee
      break
    case '水费': unitPrice = settingsData.value.waterFee
      break
    case '电费': unitPrice = settingsData.value.electricityFee
      break
    case '燃气费': unitPrice = settingsData.value.gasFee
      break
    default:
      return
  }
  
  formData.value.amount = Number((formData.value.usage * unitPrice).toFixed(2))
}

// 处理批量新增费用类型变更
const handleBatchTypeChange = () => {
  batchFormData.value.usage = 0
  batchFormData.value.amount = 0
}

// 计算批量新增金额
const calculateBatchAmount = () => {
  if (!batchFormData.value.usage) {
    batchFormData.value.amount = 0
    return
  }
  
  let unitPrice = 0
  switch (batchFormData.value.type) {
    case '物业费': unitPrice = settingsData.value.propertyFee
      break
    case '水费': unitPrice = settingsData.value.waterFee
      break
    case '电费': unitPrice = settingsData.value.electricityFee
      break
    case '燃气费': unitPrice = settingsData.value.gasFee
      break
    default:
      return
  }
  
  batchFormData.value.amount = Number((batchFormData.value.usage * unitPrice).toFixed(2))
}

// 监听批量新增对话框的显示状态，自动搜索业主
watch(showBatchAddDialog, (newVal) => {
  if (newVal) {
    searchOwners()
  }
})

onMounted(() => {
  loadFees()
  loadOwners()
  loadSettings()
})

const loadFees = async () => {
  const params = {
    page: 1,
    size: 100,
    month: filterData.value.month ? filterData.value.month : undefined,
    status: filterData.value.status !== '' ? filterData.value.status : undefined,
    type: filterData.value.type !== '' ? filterData.value.type : undefined,
    keyword: filterData.value.keyword || undefined
  }
  const res = await request.get('/fee/list', { params })
  feeList.value = res.data.records || []
}

const resetFilter = () => {
  filterData.value = {
    month: '',
    status: '',
    type: '',
    keyword: ''
  }
  loadFees()
}

const loadOwners = async () => {
  const res = await request.get('/admin/owners')
  if (res.data) {
    ownerList.value = res.data.map(o => ({
      id: o.id || o.userId,
      userId: o.id || o.userId,
      realName: o.realName || o.username,
      username: o.username
    }))
  } else {
    ownerList.value = []
  }
}

const handleAdd = async () => {
  if (!formData.value.ownerId || !formData.value.month || formData.value.amount <= 0) {
    ElMessage.warning('请填写完整信息')
    return
  }

  await request.post('/fee/add', formData.value)
  ElMessage.success('添加成功')
  showAddDialog.value = false
  formData.value = {
    ownerId: null,
    type: '物业费',
    month: '',
    amount: 0,
    status: 0
  }
  loadFees()
}

const handleDelete = async (id) => {
  await request.delete(`/fee/${id}`)
  ElMessage.success('删除成功')
  loadFees()
}

// 批量新增相关方法
const searchOwners = async () => {
  const res = await request.get('/admin/owners')
  if (res.data) {
    let owners = res.data.map(o => ({
      id: o.id || o.userId,
      userId: o.id || o.userId,
      realName: o.realName || o.username,
      username: o.username,
      building: o.building || '',
      unit: o.unit || '',
      room: o.room || '',
      phone: o.phone || ''
    }))

    // 根据关键词筛选
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase().replace(/\s/g, '')
      owners = owners.filter(o => {
        const realName = (o.realName || '').toLowerCase().replace(/\s/g, '')
        const building = (o.building || '').toLowerCase().replace(/\s/g, '')
        const unit = (o.unit || '').toLowerCase().replace(/\s/g, '')
        const room = (o.room || '').toLowerCase().replace(/\s/g, '')
        return realName.includes(keyword) || building.includes(keyword) || unit.includes(keyword) || room.includes(keyword)
      })
    }

    searchedOwners.value = owners
  } else {
    searchedOwners.value = []
  }
}

const resetSearch = () => {
  searchKeyword.value = ''
  searchedOwners.value = []
  selectedOwners.value = []
}

const handleSelectionChange = (selection) => {
  selectedOwners.value = selection
}

const handleBatchAdd = async () => {
  if (selectedOwners.value.length === 0) {
    ElMessage.warning('请选择至少一个业主')
    return
  }

  if (!batchFormData.value.type || !batchFormData.value.month || batchFormData.value.amount <= 0) {
    ElMessage.warning('请填写完整的缴费信息')
    return
  }

  const promises = selectedOwners.value.map(owner =>
    request.post('/fee/add', {
      ownerId: owner.userId,
      type: batchFormData.value.type,
      month: batchFormData.value.month,
      amount: batchFormData.value.amount,
      status: batchFormData.value.status,
      remark: batchFormData.value.remark
    })
  )

  try {
    await Promise.all(promises)
    ElMessage.success(`成功添加 ${promises.length} 条缴费记录`)
    showBatchAddDialog.value = false
    resetSearch()
    batchFormData.value = {
      type: '物业费',
      month: '',
      amount: 0,
      status: 0
    }
    loadFees()
  } catch (error) {
    ElMessage.error('添加失败，请重试')
  }
}

// 一键催缴
const handleUrgePayment = async () => {
  try {
    await request.post('/fee/urge-payment')
    ElMessage.success('一键催缴成功，已更新未缴费记录的提醒状态')
  } catch (error) {
    console.error('一键催缴失败:', error)
    ElMessage.error('一键催缴失败，请重试')
  }
}
</script>
