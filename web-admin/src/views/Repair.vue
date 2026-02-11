<template>
  <el-card>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="待派单" name="0">
        <repair-list :status="activeTab" />
      </el-tab-pane>
      <el-tab-pane label="已派单" name="1">
        <repair-list :status="activeTab" />
      </el-tab-pane>
      <el-tab-pane label="维修中" name="2">
        <repair-list :status="activeTab" />
      </el-tab-pane>
      <el-tab-pane label="已完成" name="3">
        <repair-list :status="activeTab" />
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import RepairList from './RepairList.vue'

const activeTab = ref('0')

const handleTabChange = (tabName) => {
  activeTab.value = tabName
}
</script>
