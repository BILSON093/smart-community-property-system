<template>
  <div class="repair">
    <div class="page-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          <van-icon name="arrow-left" size="18" />
          返回
        </button>
        <h2>在线报修</h2>
      </div>
    </div>
    
    <div class="repair-form">
      <form @submit.prevent="handleSubmit">
        <div class="form-item">
          <label class="form-label">故障描述</label>
          <textarea 
            v-model="form.content" 
            class="form-textarea" 
            placeholder="请详细描述故障情况"
            rows="4"
          ></textarea>
        </div>
        

        
        <div class="form-item">
          <label class="form-label">上传图片</label>
          <div class="uploader">
            <div class="file-list" v-for="(file, index) in fileList" :key="index">
              <img :src="file.url" alt="上传图片" class="file-preview" />
              <button type="button" class="file-remove" @click="removeFile(index)">×</button>
            </div>
            <div class="upload-area" @click="triggerFileInput">
              <input 
                ref="fileInput" 
                type="file" 
                multiple 
                accept="image/*" 
                @change="handleFileChange"
                style="display: none;"
              />
              <div class="upload-icon">+</div>
              <div class="upload-text">点击上传图片</div>
            </div>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="submit-btn">提交报修</button>
        </div>
      </form>
    </div>

    <div class="section-header">
      <h3>我的报修记录</h3>
    </div>

    <div class="repair-list">
      <div v-if="list.length === 0" class="empty-state">
        <p>暂无报修记录</p>
      </div>
      <div v-else class="repair-item" v-for="item in list" :key="item.id">
        <div class="repair-item-header">
          <span class="repair-content">{{ item.content }}</span>
          <span :class="['repair-status', `status-${item.status}`]">
            {{ getStatusText(item.status) }}
          </span>
        </div>
        <div class="repair-item-footer">
          <span class="repair-time">{{ item.createTime }}</span>
          <div class="footer-right">
            <div v-if="item.images" class="repair-images">
              <img 
                v-for="(img, index) in JSON.parse(item.images)" 
                :key="index" 
                :src="img" 
                alt="故障图片" 
                class="repair-image"
              />
            </div>
            <button 
              v-if="item.status === 3 && !item.evaluation" 
              class="evaluate-btn"
              @click="showEvaluateDialog(item)"
            >
              评价
            </button>
            <div v-if="item.evaluation" class="evaluation-info">
              <span class="evaluation-score">{{ item.evaluation.score }}星</span>
              <span class="evaluation-comment">{{ item.evaluation.comment }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 评价对话框 -->
    <div v-if="showEvaluateModal" class="modal-overlay" @click="closeEvaluateDialog">
      <div class="modal-content" @click.stop>
        <h3 class="modal-title">评价维修服务</h3>
        <div class="form-item">
          <label class="form-label">评分</label>
          <div class="star-rating">
            <span 
              v-for="star in 5" 
              :key="star"
              :class="['star', { active: evaluateForm.score >= star }]"
              @click="evaluateForm.score = star"
            >★</span>
          </div>
        </div>
        <div class="form-item">
          <label class="form-label">评价内容</label>
          <textarea 
            v-model="evaluateForm.comment" 
            class="form-textarea" 
            placeholder="请输入您的评价" 
            rows="3"
          ></textarea>
        </div>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeEvaluateDialog">取消</button>
          <button class="btn-submit" @click="submitEvaluation">提交评价</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const form = ref({
  content: '',
  images: ''
})
const fileList = ref([])
const list = ref([])
const loading = ref(false)
const fileInput = ref(null)

// 评价相关
const showEvaluateModal = ref(false)
const evaluateForm = ref({ score: 5, comment: '' })
const currentEvaluateRepairId = ref(null)

const handleSubmit = async () => {
  if (!form.value.content) {
    showToast('请输入故障描述')
    return
  }

  // 上传图片
  const imageUrls = []
  for (let file of fileList.value) {
    const res = await uploadImage(file.file)
    imageUrls.push(res.url)
  }
  form.value.images = JSON.stringify(imageUrls)

  await request.post('/repair/add', form.value)
  showToast('报修成功')
  form.value.content = ''
  fileList.value = []
  onLoad()
}

const uploadImage = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  const res = await request.post('/common/upload', formData)
  // 兼容不同的返回格式
  if (res.data && res.data.url) {
    return { url: res.data.url }
  } else if (res.url) {
    return { url: res.url }
  } else {
    throw new Error('图片上传失败')
  }
}

const onLoad = async () => {
  const res = await request.get('/repair/my', { params: { page: 1, size: 100 } })
  list.value = res.data.records || []
}

const getStatusText = (status) => {
  const map = { 0: '待派单', 1: '已派单', 2: '维修中', 3: '已完成' }
  return map[status]
}

const triggerFileInput = () => {
  if (fileList.value.length >= 3) {
    showToast('最多只能上传3张图片')
    return
  }
  fileInput.value.click()
}

const handleFileChange = (e) => {
  const files = e.target.files
  if (files.length > 0) {
    for (let i = 0; i < files.length; i++) {
      if (fileList.value.length >= 3) break
      const file = files[i]
      const reader = new FileReader()
      reader.onload = (e) => {
        fileList.value.push({
          file: file,
          url: e.target.result
        })
      }
      reader.readAsDataURL(file)
    }
  }
}


const removeFile = (index) => {
  fileList.value.splice(index, 1)
}

const goBack = () => {
  router.back()
}

// 评价相关方法
const showEvaluateDialog = (item) => {
  currentEvaluateRepairId.value = item.id
  evaluateForm.value = { score: 5, comment: '' }
  showEvaluateModal.value = true
}

const closeEvaluateDialog = () => {
  showEvaluateModal.value = false
  currentEvaluateRepairId.value = null
}

const submitEvaluation = async () => {
  if (!evaluateForm.value.comment) {
    showToast('请输入评价内容')
    return
  }

  await request.post('/repair/evaluate', {
    repairId: currentEvaluateRepairId.value,
    score: evaluateForm.value.score,
    comment: evaluateForm.value.comment
  })

  showToast('评价成功')
  closeEvaluateDialog()
  onLoad() // 重新加载数据，显示评价结果
}

onMounted(() => {
  onLoad()
})

</script>

<style scoped>
.repair {
  min-height: 100vh;
}

.page-header {
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-back {
  background: #f0f9ff;
  color: #409EFF;
  border: 1px solid #d9ecff;
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  font-weight: 500;
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.1);
}

.btn-back:hover {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  transform: translateY(-1px);
}

.btn-back:active {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #304156;
  margin: 0;
}

.repair-form {
  background: #f9fafc;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #304156;
  margin-bottom: 8px;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.3s;
}

.form-textarea:focus {
  outline: none;
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.uploader {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.file-list {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.file-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-remove {
  position: absolute;
  top: 0;
  right: 0;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  border-radius: 0 4px 0 4px;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

.file-remove:hover {
  background: rgba(0, 0, 0, 0.7);
}

.upload-area {
  width: 100px;
  height: 100px;
  border: 2px dashed #dcdfe6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #409EFF;
  background-color: rgba(64, 158, 255, 0.05);
}

.upload-icon {
  font-size: 24px;
  color: #c0c4cc;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
  color: #909399;
}

.form-actions {
  margin-top: 30px;
  text-align: right;
}

.type-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 8px;
}

.type-btn {
  padding: 8px 20px;
  border: 1px solid #dcdfe6;
  border-radius: 20px;
  background: white;
  color: #606266;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.type-btn:hover {
  border-color: #409EFF;
  color: #409EFF;
}

.type-btn.active {
  background: #409EFF;
  color: white;
  border-color: #409EFF;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.submit-btn {
  background-color: #409EFF;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.submit-btn:hover {
  background-color: #66b1ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.submit-btn:active {
  transform: translateY(1px);
}

.section-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.section-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #304156;
  margin: 0;
}

.repair-list {
  margin-top: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 14px;
}

.repair-item {
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  background: #f9fafc;
  transition: all 0.3s;
}

.repair-item:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.repair-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.repair-content {
  flex: 1;
  font-size: 14px;
  color: #304156;
  line-height: 1.5;
}

.repair-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  margin-left: 16px;
}

.status-0 {
  background: #ecf5ff;
  color: #409EFF;
}

.status-1 {
  background: #e6f7ff;
  color: #1890ff;
}

.status-2 {
  background: #f6ffed;
  color: #67c23a;
}

.status-3 {
  background: #f0f9ff;
  color: #909399;
}

.repair-item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.repair-time {
  flex-shrink: 0;
}

.repair-images {
  display: flex;
  gap: 8px;
}

.repair-image {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.evaluate-btn {
  padding: 6px 16px;
  background: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.evaluate-btn:hover {
  background: #66b1ff;
}

.evaluation-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.evaluation-score {
  color: #E6A23C;
  font-weight: 500;
}

.evaluation-comment {
  color: #909399;
}

/* 评价对话框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 24px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  color: #304156;
  margin-bottom: 20px;
  text-align: center;
}

.star-rating {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.star {
  font-size: 24px;
  color: #dcdfe6;
  cursor: pointer;
  transition: all 0.3s;
}

.star:hover, .star.active {
  color: #E6A23C;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.btn-cancel {
  padding: 8px 20px;
  background: #f5f7fa;
  color: #606266;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #e4e7ed;
}

.btn-submit {
  padding: 8px 20px;
  background: #409EFF;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-submit:hover {
  background: #66b1ff;
}
</style>
