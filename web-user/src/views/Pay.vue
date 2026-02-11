<template>
  <div class="pay">
    <div class="page-header">
      <div class="header-left">
        <button @click="goBack" class="btn-back">
          <van-icon name="arrow-left" size="18" />
          返回
        </button>
        <h2>物业缴费</h2>
      </div>
    </div>
    
    <div class="payment-section">
      <h3 class="section-title">待缴费清单</h3>
      <div class="payment-list">
        <div v-if="list.length === 0" class="empty-state">
          <p>暂无待缴费项目</p>
        </div>
        <div v-else class="payment-item" v-for="item in list" :key="item.id">
          <div class="payment-info">
            <div class="payment-type">
              <span class="type-name">{{ item.month }}{{ item.type }}费</span>
              <span class="payment-status" :class="['status-' + item.status]">
                {{ item.status === 0 ? '待缴' : '已缴' }}
              </span>
            </div>
            <div class="payment-details">
                <div class="detail-item">
                  <label>账单月份：</label>
                  <span>{{ item.month }}</span>
                </div>
                <div class="detail-item">
                  <label></label>
                  <span>{{ item.deadline }}</span>
                </div>
                <div class="detail-item" v-if="item.remark">
                  <label>备注：</label>
                  <span>{{ item.remark }}</span>
                </div>
              </div>
          </div>
          <div class="payment-actions">
            <div class="amount">¥{{ item.amount }}</div>
            <button 
              v-if="item.status === 0" 
              class="btn-pay" 
              @click="handlePay(item)"
            >
              立即缴费
            </button>
            <span v-else class="paid-label">已缴费</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付确认弹窗 -->
    <div v-if="showPayDialog" class="pay-dialog-overlay" @click="closeDialog">
      <div class="pay-dialog" @click.stop>
        <div class="dialog-header">
          <h3>确认支付</h3>
          <button class="dialog-close" @click="closeDialog">×</button>
        </div>
        <div class="dialog-content">
          <div class="payment-summary">
            <h4>支付详情</h4>
            <div class="summary-item">
              <label>缴费项目：</label>
              <span>{{ currentFee?.month }}{{ currentFee?.type }}费</span>
            </div>
            <div class="summary-item">
              <label>支付金额：</label>
              <span class="summary-amount">¥{{ currentFee?.amount }}</span>
            </div>
            <div class="summary-item">
              <label></label>
              <span>{{ currentFee?.deadline }}</span>
            </div>
            <div class="summary-item" v-if="currentFee?.remark">
              <label>备注：</label>
              <span>{{ currentFee?.remark }}</span>
            </div>
          </div>
          
          <div class="payment-methods">
            <h4>支付方式</h4>
            <div class="method-item" v-for="method in paymentMethods" :key="method.id" @click="selectMethod(method)">
              <input type="radio" :id="method.id" :name="method.name" v-model="selectedMethod" :value="method.id" />
              <label :for="method.id">{{ method.name }}</label>
              <img :src="method.icon" class="method-icon" alt="">
            </div>
          </div>
        </div>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="closeDialog">取消</button>
          <button class="btn-confirm" @click="confirmPay">确认支付</button>
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
const list = ref([])
const loading = ref(false)
const showPayDialog = ref(false)
const currentFee = ref(null)
const selectedMethod = ref('alipay')
const paymentMethods = ref([
  { id: 'alipay', name: '支付宝', icon: '/images/zhifubao.png' },
  { id: 'wechat', name: '微信支付', icon: '/images/weixin.png' },
  { id: 'bank', name: '银行卡', icon: '/images/yinghangka.png' }
])

const onLoad = async () => {
  loading.value = true
  try {
    const res = await request.get('/fee/my', { params: { page: 1, size: 100 } })
    list.value = res.data.records || []
  } catch (error) {
    console.error('加载缴费列表失败:', error)
    showToast('加载缴费列表失败')
  } finally {
    loading.value = false
  }
}

const handlePay = (item) => {
  currentFee.value = item
  showPayDialog.value = true
}

const confirmPay = async () => {
  if (!currentFee.value) return
  
  try {
    await request.post(`/fee/pay/${currentFee.value.id}`)
    showToast('支付成功')
    closeDialog()
    onLoad()
  } catch (error) {
    console.error('支付失败:', error)
    showToast('支付失败，请重试')
  }
}

const closeDialog = () => {
  showPayDialog.value = false
  currentFee.value = null
}

const selectMethod = (method) => {
  selectedMethod.value = method.id
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  onLoad()
})
</script>

<style scoped>
.pay {
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

.payment-section {
  background: #f9fafc;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #304156;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.payment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 14px;
}

.payment-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.payment-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.payment-info {
  flex: 1;
  margin-right: 20px;
}

.payment-type {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.type-name {
  font-size: 16px;
  font-weight: 500;
  color: #304156;
}

.payment-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-0 {
  background: #ecf5ff;
  color: #409EFF;
}

.status-1 {
  background: #f6ffed;
  color: #67C23A;
}

.payment-details {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #606266;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-item label {
  color: #909399;
}

.payment-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.amount {
  font-size: 20px;
  font-weight: 600;
  color: #f56c6c;
}

.btn-pay {
  background-color: #409EFF;
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-pay:hover {
  background-color: #66b1ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.btn-pay:active {
  transform: translateY(1px);
}

.paid-label {
  font-size: 14px;
  color: #67C23A;
  font-weight: 500;
}

/* 支付弹窗样式 */
.pay-dialog-overlay {
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

.pay-dialog {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e4e7ed;
  background: #f9fafc;
}

.dialog-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #304156;
  margin: 0;
}

.dialog-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #909399;
  cursor: pointer;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.dialog-close:hover {
  background: #f0f2f5;
  color: #606266;
}

.dialog-content {
  padding: 24px;
}

.payment-summary {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f2f5;
}

.payment-summary h4,
.payment-methods h4 {
  font-size: 14px;
  font-weight: 600;
  color: #304156;
  margin: 0 0 16px 0;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-item label {
  color: #909399;
}

.summary-amount {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

.payment-methods {
  margin-bottom: 20px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-item:hover {
  border-color: #409EFF;
  background: #ecf5ff;
}

.method-item input[type="radio"] {
  margin-right: 12px;
}

.method-item label {
  flex: 1;
  font-size: 14px;
  color: #304156;
  cursor: pointer;
}

.method-icon {
  width: 64px;
  height: 32px;
  object-fit: contain;
  border-radius: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e4e7ed;
  background: #f9fafc;
}

.btn-cancel {
  background: white;
  color: #606266;
  border: 1px solid #dcdfe6;
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  border-color: #c6e2ff;
  color: #409EFF;
}

.btn-confirm {
  background-color: #409EFF;
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm:hover {
  background-color: #66b1ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

@media (max-width: 768px) {
  .payment-details {
    flex-direction: column;
    gap: 8px;
  }
  
  .payment-item {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .payment-actions {
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid #f0f2f5;
  }
}
</style>
