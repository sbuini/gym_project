<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const user = ref(null)
const stats = ref({
  totalBookings: 0,
  completedBookings: 0,
  totalSpent: 0,
  balance: 0
})
const rechargeDialogVisible = ref(false)
const rechargeAmount = ref(100)

const handleRecharge = async () => {
  if (rechargeAmount.value <= 0) return
  try {
    const res = await axios.post(`/api/users/${user.value.id}/recharge`, { amount: rechargeAmount.value })
    if (res.data.success) {
      ElMessage.success('充值成功')
      rechargeDialogVisible.value = false
      load()
    }
  } catch (e) {
    ElMessage.error('充值失败')
  }
}

const load = async () => {
  const savedUser = localStorage.getItem('gym_user')
  if (!savedUser) {
    router.push('/login')
    return
  }
  const current = JSON.parse(savedUser)
  loading.value = true
  try {
    const [userRes, statsRes] = await Promise.all([
      axios.get(`/api/users/${current.id}`),
      axios.get(`/api/users/${current.id}/stats`)
    ])
    user.value = userRes.data
    stats.value = statsRes.data
  } catch (e) {
    console.error(e)
    ElMessage.error('加载个人信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="profile-page" v-loading="loading">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">个人信息</h3>
        <div class="text-muted">查看当前账号的基础信息</div>
      </div>
      <el-button type="primary" icon="Setting" round @click="$router.push('/account-settings')">
        账号设置
      </el-button>
    </div>

    <el-card shadow="never" class="card mb-4">
      <template #header>
        <div class="fw-bold">基础信息</div>
      </template>
      <el-descriptions v-if="user" :column="2" size="large" border>
        <el-descriptions-item label="用户编号">{{ user.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ user.username }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ user.email || '-' }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag v-if="user.role === 'ROLE_ADMIN'" type="warning" effect="light" round>系统管理员</el-tag>
          <el-tag v-else type="success" effect="light" round>普通用户</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂无用户信息" />
    </el-card>

    <div class="row g-4 mb-4" v-if="user">
      <div class="col-md-3">
        <div class="stat-glass-card-mini balance-card" @click="rechargeDialogVisible = true">
          <div class="stat-icon-mini bg-orange"><el-icon><Wallet /></el-icon></div>
          <div class="stat-info-mini">
            <div class="stat-label-mini">账户余额 (元)</div>
            <div class="stat-value-mini">{{ stats.balance.toFixed(2) }}</div>
          </div>
          <div class="recharge-btn-mini">充值</div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-glass-card-mini">
          <div class="stat-icon-mini bg-blue"><el-icon><Tickets /></el-icon></div>
          <div class="stat-info-mini">
            <div class="stat-label-mini">总预约数</div>
            <div class="stat-value-mini">{{ stats.totalBookings }}</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-glass-card-mini">
          <div class="stat-icon-mini bg-green"><el-icon><CircleCheck /></el-icon></div>
          <div class="stat-info-mini">
            <div class="stat-label-mini">已完成预约</div>
            <div class="stat-value-mini">{{ stats.completedBookings }}</div>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="stat-glass-card-mini">
          <div class="stat-icon-mini bg-red"><el-icon><Money /></el-icon></div>
          <div class="stat-info-mini">
            <div class="stat-label-mini">累计消费 (元)</div>
            <div class="stat-value-mini">{{ stats.totalSpent.toFixed(2) }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recharge Dialog -->
    <el-dialog v-model="rechargeDialogVisible" title="账户充值" width="400px" align-center>
      <div class="p-3">
        <div class="mb-4">
          <div class="text-muted mb-2">选择充值金额</div>
          <el-radio-group v-model="rechargeAmount" size="large" class="w-100 recharge-options">
            <el-radio-button :label="50">50元</el-radio-button>
            <el-radio-button :label="100">100元</el-radio-button>
            <el-radio-button :label="200">200元</el-radio-button>
            <el-radio-button :label="500">500元</el-radio-button>
          </el-radio-group>
        </div>
        <div class="mb-4">
          <div class="text-muted mb-2">自定义金额</div>
          <el-input-number v-model="rechargeAmount" :min="1" :max="10000" class="w-100" />
        </div>
        <div class="recharge-tips small text-muted p-3 bg-light rounded-3 mb-4">
          <el-icon class="me-1"><InfoFilled /></el-icon>
          充值后余额将立即生效，可用于支付场馆预约费用。
        </div>
      </div>
      <template #footer>
        <el-button @click="rechargeDialogVisible = false" round>取消</el-button>
        <el-button type="primary" @click="handleRecharge" round>立即支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }
.profile-page {
  max-width: 980px;
  margin: 0 auto;
}
.card {
  border-radius: 24px;
  border: none;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.stat-glass-card-mini {
  background: white;
  padding: 20px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
  border: 1px solid rgba(0,0,0,0.02);
}

.stat-icon-mini {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  margin-right: 16px;
}

.bg-blue { background: linear-gradient(135deg, #409EFF 0%, #64b5f6 100%); }
.bg-green { background: linear-gradient(135deg, #67C23A 0%, #95de64 100%); }
.bg-orange { background: linear-gradient(135deg, #fa8c16 0%, #ffd666 100%); }
.bg-red { background: linear-gradient(135deg, #f5222d 0%, #ff7875 100%); }

.balance-card {
  cursor: pointer;
  position: relative;
  border: 1px solid rgba(250, 140, 22, 0.2);
  transition: all 0.3s;
}

.balance-card:hover {
  border-color: #fa8c16;
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(250, 140, 22, 0.1);
}

.recharge-btn-mini {
  position: absolute;
  right: 15px;
  top: 15px;
  font-size: 11px;
  color: #fa8c16;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 6px;
  background: rgba(250, 140, 22, 0.1);
}

.recharge-options {
  display: flex;
  justify-content: space-between;
}

.recharge-options :deep(.el-radio-button__inner) {
  border-radius: 10px !important;
  border: 1px solid #dcdfe6 !important;
  margin-right: 8px;
  width: 75px;
}

.recharge-options :deep(.el-radio-button:last-child .el-radio-button__inner) {
  margin-right: 0;
}

.recharge-options :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #409EFF !important;
  border-color: #409EFF !important;
  color: white !important;
}

.stat-label-mini { color: #909399; font-size: 13px; margin-bottom: 2px; }
.stat-value-mini { font-size: 24px; font-weight: 800; color: #303133; line-height: 1; }
</style>
