<template>
  <div class="pay">
    <div class="page-header">
      <div class="header-left">
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
              <span class="type-name">{{ item.month }}{{ item.type }}</span>
              <span class="payment-status" :class="['status-' + item.status]">
                {{ item.status === 0 ? '待缴' : '已缴' }}
              </span>
            </div>
            <div class="payment-details">
                <div class="detail-item">
                  <label>账单月份：</label>
                  <span>{{ item.month }}</span>
                </div>
                <div class="detail-item" v-if="calculateUsage(item)">
                  <label>使用量：</label>
                  <span>{{ calculateUsage(item).usage }} {{ calculateUsage(item).unit }}</span>
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
              <span>{{ currentFee?.month }}{{ currentFee?.type }}</span>
            </div>
            <div class="summary-item" v-if="calculateUsage(currentFee)">
              <label>使用量：</label>
              <span>{{ calculateUsage(currentFee).usage }} {{ calculateUsage(currentFee).unit }}</span>
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
const feeSettings = ref(null)
const paymentMethods = ref([
  { id: 'alipay', name: '支付宝', icon: '/images/zhifubao.png' },
  { id: 'wechat', name: '微信支付', icon: '/images/weixin.png' },
  { id: 'bank', name: '银行卡', icon: '/images/yinghangka.png' }
])

const calculateUsage = (item) => {
  if (!feeSettings.value || !item.amount) return null
  
  const amount = parseFloat(item.amount)
  let unit = ''
  let usage = 0
  
  if (item.type === '水费') {
    usage = amount / parseFloat(feeSettings.value.waterFee)
    unit = '吨'
  } else if (item.type === '电费') {
    usage = amount / parseFloat(feeSettings.value.electricityFee)
    unit = '度'
  } else if (item.type === '燃气费') {
    usage = amount / parseFloat(feeSettings.value.gasFee)
    unit = '立方米'
  } else {
    return null
  }
  
  return { usage: usage.toFixed(2), unit }
}

const onLoad = async () => {
  loading.value = true
  try {
    const [feesRes, settingsRes] = await Promise.all([
      request.get('/fee/my', { params: { page: 1, size: 100 } }),
      request.get('/fee/settings')
    ])
    list.value = feesRes.data.records || []
    feeSettings.value = settingsRes.data
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
    const orderRes = await request.post(`/fee/pay/order/${currentFee.value.id}`, {
      channel: selectedMethod.value
    })
    const orderNo = orderRes.data?.orderNo
    if (!orderNo) {
      throw new Error('创建支付订单失败')
    }
    const payRes = await request.post(`/fee/pay/simulate/${orderNo}`)
    const idempotent = payRes.data?.idempotent
    showToast(idempotent ? '订单已支付' : `支付成功，订单号 ${orderNo}`)
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
  border-bottom: 1px solid #F1F5F9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.btn-back {
  background: var(--primary-bg);
  color: var(--primary);
  border: 1px solid rgba(79, 110, 247, 0.2);
  padding: 8px 20px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  font-weight: 500;
}

.btn-back:hover {
  background: rgba(79, 110, 247, 0.12);
}

.page-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.payment-section {
  background: var(--bg-card);
  border-radius: var(--radius-sm);
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-sm);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #F1F5F9;
}

.payment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
  color: var(--text-muted);
  font-size: 14px;
}

.payment-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: var(--bg-card);
  border-radius: var(--radius-sm);
  padding: 20px;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s;
}

.payment-item:hover {
  box-shadow: var(--shadow-hover);
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
  color: var(--text-primary);
}

.payment-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status-0 {
  background: var(--primary-bg);
  color: var(--primary);
}

.status-1 {
  background: rgba(16, 185, 129, 0.1);
  color: var(--success);
}

.payment-details {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: var(--text-secondary);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
}

.detail-item label {
  color: var(--text-muted);
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
  color: var(--danger);
}

.btn-pay {
  background: var(--primary);
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-pay:hover {
  background: var(--primary-light);
  box-shadow: 0 4px 12px rgba(79, 110, 247, 0.3);
}

.btn-pay:active {
  transform: translateY(1px);
}

.paid-label {
  font-size: 14px;
  color: var(--success);
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
  border-radius: var(--radius-lg);
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
  border-bottom: 1px solid #F1F5F9;
  background: #F8FAFC;
}

.dialog-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.dialog-close {
  background: none;
  border: none;
  font-size: 20px;
  color: var(--text-muted);
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
  background: #F1F5F9;
  color: var(--text-secondary);
}

.dialog-content {
  padding: 24px;
}

.payment-summary {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #F1F5F9;
}

.payment-summary h4,
.payment-methods h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
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
  color: var(--text-muted);
}

.summary-amount {
  font-size: 18px;
  font-weight: 600;
  color: var(--danger);
}

.payment-methods {
  margin-bottom: 20px;
}

.method-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #E2E8F0;
  border-radius: var(--radius-sm);
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.method-item:hover {
  border-color: var(--primary);
  background: var(--primary-bg);
}

.method-item input[type="radio"] {
  margin-right: 12px;
}

.method-item label {
  flex: 1;
  font-size: 14px;
  color: var(--text-primary);
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
  border-top: 1px solid #F1F5F9;
  background: #F8FAFC;
}

.btn-cancel {
  background: white;
  color: var(--text-secondary);
  border: 1px solid #E2E8F0;
  padding: 8px 20px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  border-color: rgba(79, 110, 247, 0.3);
  color: var(--primary);
}

.btn-confirm {
  background: var(--primary);
  color: white;
  border: none;
  padding: 8px 24px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-confirm:hover {
  background: var(--primary-light);
  box-shadow: 0 2px 8px rgba(79, 110, 247, 0.3);
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
