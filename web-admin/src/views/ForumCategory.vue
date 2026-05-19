<template>
  <div class="page-container">
    <el-card class="page-card">
      <template #header>
        <div class="page-header">
          <div class="page-title">
            <div class="title-bar"></div>
            <span>论坛分类管理</span>
          </div>
          <div class="page-actions">
            <el-button type="primary" @click="showAddDialog = true">新增分类</el-button>
          </div>
        </div>
      </template>

      <el-table :data="categoryList" border stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" width="180" />
      <el-table-column prop="description" label="描述" width="300"/>
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadCategories"
      @current-change="loadCategories"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <el-dialog v-model="showAddDialog" title="新增分类" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="showEditDialog" title="编辑分类" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="分类名称">
          <el-input v-model="formData.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="formData.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">确定</el-button>
      </template>
    </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const formData = ref({
  id: null,
  name: '',
  description: '',
  sortOrder: 0
})

onMounted(() => {
  loadCategories()
})

const loadCategories = async () => {
  const res = await request.get('/forum/category/list/page', {
    params: { page: currentPage.value, size: pageSize.value }
  })
  categoryList.value = res.data.records || []
  total.value = res.data.total || 0
}

const resetFormData = () => {
  formData.value = {
    id: null,
    name: '',
    description: '',
    sortOrder: 0
  }
}

const handleAdd = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  await request.post('/forum/category/add', formData.value)
  ElMessage.success('添加成功')
  showAddDialog.value = false
  resetFormData()
  loadCategories()
}

const handleEdit = (row) => {
  formData.value = {
    id: row.id,
    name: row.name,
    description: row.description,
    sortOrder: row.sortOrder
  }
  showEditDialog.value = true
}

const handleUpdate = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  await request.put('/forum/category/update', formData.value)
  ElMessage.success('更新成功')
  showEditDialog.value = false
  resetFormData()
  loadCategories()
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确认删除该分类吗?', '提示', { type: 'warning' })
  await request.delete(`/forum/category/${id}`)
  ElMessage.success('删除成功')
  loadCategories()
}
</script>
