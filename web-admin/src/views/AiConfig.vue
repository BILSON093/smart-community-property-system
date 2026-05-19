<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>AI模型配置</span>
          </div>
          <div class="page-actions">
            <el-button type="primary" @click="handleAdd">添加配置</el-button>
          </div>
        </div>
      </template>

      <el-table :data="configList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="provider" label="服务商" width="120">
          <template #default="{ row }">
            <el-tag :type="getProviderTagType(row.provider)">
              {{ getProviderLabel(row.provider) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="apiUrl" label="API URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="modelName" label="模型名称" width="150" />
        <el-table-column prop="apiKey" label="API Key" width="150" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.apiKey ? maskApiKey(row.apiKey) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="enabled" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.enabled === 1 ? 'success' : 'info'">
              {{ row.enabled === 1 ? '已启用' : '未启用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.enabled !== 1"
              type="success"
              size="small"
              @click="handleEnable(row)"
            >
              启用
            </el-button>
            <el-button
              v-if="row.enabled === 1"
              type="warning"
              size="small"
              @click="handleDisable(row)"
            >
              禁用
            </el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="服务商" prop="provider">
          <el-select v-model="form.provider" placeholder="请选择服务商" @change="handleProviderChange">
            <el-option label="本地模型" value="local" />
            <el-option label="小米MiMo" value="xiaomimimo" />
            <el-option label="ModelScope" value="modelscope" />
            <el-option label="阿里云通义千问" value="dashscope" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="API URL" prop="apiUrl">
          <el-input v-model="form.apiUrl" placeholder="请输入API URL" />
        </el-form-item>
        <el-form-item label="API Key" prop="apiKey">
          <el-input v-model="form.apiKey" type="password" placeholder="请输入API Key（本地模型可留空）" show-password />
        </el-form-item>
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="form.modelName" placeholder="请输入模型名称" />
        </el-form-item>
        <el-form-item label="支持多模态">
          <el-checkbox v-model="form.supportsMultimodal" :true-label="1" :false-label="0">
            支持图片识别（多模态模型）
          </el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const configList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('添加配置')
const formRef = ref()
const isEdit = ref(false)

const form = ref({
  id: null,
  provider: '',
  apiUrl: '',
  apiKey: '',
  modelName: '',
  supportsMultimodal: 0
})

const rules = {
  provider: [{ required: true, message: '请选择服务商', trigger: 'change' }],
  apiUrl: [{ required: true, message: '请输入API URL', trigger: 'blur' }],
  modelName: [{ required: true, message: '请输入模型名称', trigger: 'blur' }]
}

const providerPresets = {
  local: {
    apiUrl: 'http://localhost:1234/v1/chat/completions',
    modelName: 'local-model'
  },
  xiaomimimo: {
    apiUrl: 'https://api.xiaomimimo.com/v1/chat/completions',
    modelName: 'mimo-v2-flash'
  },
  modelscope: {
    apiUrl: 'https://api-inference.modelscope.cn/v1/chat/completions',
    modelName: 'ZhipuAI/GLM-4.7'
  },
  dashscope: {
    apiUrl: 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions',
    modelName: 'qwen-plus'
  },
  custom: {
    apiUrl: '',
    modelName: ''
  }
}

onMounted(() => {
  loadConfigs()
})

const loadConfigs = async () => {
  try {
    const res = await request.get('/ai-config/list')
    if (res.code === 200) {
      configList.value = res.data || []
    }
  } catch (error) {
    ElMessage.error('加载配置失败')
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '添加配置'
  form.value = {
    id: null,
    provider: '',
    apiUrl: '',
    apiKey: '',
    modelName: '',
    supportsMultimodal: 0
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑配置'
  form.value = {
    id: row.id,
    provider: row.provider,
    apiUrl: row.apiUrl,
    apiKey: row.apiKey,
    modelName: row.modelName,
    supportsMultimodal: row.supportsMultimodal || 0
  }
  dialogVisible.value = true
}

const handleProviderChange = (value) => {
  const preset = providerPresets[value]
  if (preset) {
    form.value.apiUrl = preset.apiUrl
    form.value.modelName = preset.modelName
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    const url = isEdit.value ? '/ai-config/update' : '/ai-config/add'
    const res = await request.post(url, form.value)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
      dialogVisible.value = false
      loadConfigs()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.post(`/ai-config/delete/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadConfigs()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleEnable = (row) => {
  ElMessageBox.confirm('确定要启用该配置吗？启用后将禁用其他配置。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.post(`/ai-config/enable/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('启用成功')
        loadConfigs()
      } else {
        ElMessage.error(res.message || '启用失败')
      }
    } catch (error) {
      ElMessage.error('启用失败')
    }
  }).catch(() => {})
}

const handleDisable = (row) => {
  ElMessageBox.confirm('确定要禁用该配置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await request.post(`/ai-config/disable/${row.id}`)
      if (res.code === 200) {
        ElMessage.success('禁用成功')
        loadConfigs()
      } else {
        ElMessage.error(res.message || '禁用失败')
      }
    } catch (error) {
      ElMessage.error('禁用失败')
    }
  }).catch(() => {})
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
}

const getProviderLabel = (provider) => {
  const labels = {
    local: '本地模型',
    xiaomimimo: '小米MiMo',
    modelscope: 'ModelScope',
    dashscope: '阿里云通义',
    custom: '自定义'
  }
  return labels[provider] || provider
}

const getProviderTagType = (provider) => {
  const types = {
    local: 'info',
    xiaomimimo: 'warning',
    modelscope: 'success',
    dashscope: 'primary',
    custom: ''
  }
  return types[provider] || ''
}

const maskApiKey = (apiKey) => {
  if (!apiKey || apiKey.length <= 8) {
    return '***'
  }
  return apiKey.substring(0, 4) + '****' + apiKey.substring(apiKey.length - 4)
}
</script>

<style scoped>
</style>
