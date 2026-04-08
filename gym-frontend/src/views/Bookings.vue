<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const bookings = ref([])
const currentUser = ref(null)
const loading = ref(false)

const fetchBookings = async () => {
  const savedUser = localStorage.getItem('gym_user')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
    loading.value = true
    try {
      const response = await axios.get(`/api/bookings/${currentUser.value.id}`)
      bookings.value = response.data
    } catch (error) {
      console.error('获取预约记录失败:', error)
      ElMessage.error('无法加载预约记录')
    } finally {
      loading.value = false
    }
  }
}

const refresh = async () => {
  await fetchBookings()
  ElMessage.success('已刷新')
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const getStatusText = (status, attended) => {
  if (status === 'CONFIRMED' && attended) return '已签到'
  switch (status) {
    case 'PENDING': return '待审核'
    case 'CONFIRMED': return '已确认'
    case 'CANCELLED': return '已拒绝'
    default: return status
  }
}

const getStatusType = (status, attended) => {
  if (status === 'CONFIRMED' && attended) return 'info'
  switch (status) {
    case 'PENDING': return 'warning'
    case 'CONFIRMED': return 'success'
    case 'CANCELLED': return 'danger'
    default: return 'info'
  }
}

const signIn = async (row) => {
  try {
    const res = await axios.post(`/api/bookings/${row.id}/sign-in`, null, {
      params: { userId: currentUser.value.id }
    })
    if (res.data.success) {
      ElMessage.success('签到成功！祝您运动愉快')
      fetchBookings()
    } else {
      ElMessage.error(res.data.message || '签到失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('签到请求失败')
  }
}

const cancelBooking = (row) => {
  if (!currentUser.value) {
    return
  }
  ElMessageBox.confirm('确定取消该预约申请吗？', '取消确认', {
    confirmButtonText: '确定取消',
    cancelButtonText: '返回',
    type: 'warning',
  }).then(async () => {
    try {
      await axios.post(`/api/bookings/${row.id}/cancel`, null, {
        params: { userId: currentUser.value.id }
      })
      ElMessage.success('已取消')
      fetchBookings()
    } catch (e) {
      console.error(e)
      ElMessage.error('取消失败')
    }
  })
}

onMounted(fetchBookings)
</script>

<template>
  <div class="bookings-page" v-loading="loading">
    <div class="header-glass mb-5">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h2 class="fw-bold mb-1">我的预约记录</h2>
          <div class="text-muted">管理您的运动计划，查看审核进度</div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <div class="stat-pill">
            <span class="lbl">全部记录</span>
            <span class="val">{{ bookings.length }}</span>
          </div>
          <el-button type="primary" icon="Refresh" class="refresh-btn" @click="refresh" circle></el-button>
        </div>
      </div>
    </div>
    
    <div class="table-container-modern">
      <el-table 
        :data="bookings" 
        style="width: 100%" 
        size="large"
        class="custom-table"
        :header-cell-style="{ background: '#f8fafc', color: '#606266', fontWeight: '700', padding: '20px 0' }"
        :cell-style="{ padding: '20px 0' }"
      >
        <el-table-column label="场馆信息" min-width="250">
          <template #default="scope">
            <div class="d-flex align-items-center">
              <div class="gym-mini-img">
                <img v-if="scope.row.gym?.imageUrl" :src="scope.row.gym.imageUrl" />
                <el-icon v-else><Picture /></el-icon>
              </div>
              <div class="ms-3">
                <div class="fw-bold text-dark">{{ scope.row.gym?.name }}</div>
                <div class="text-muted x-small">{{ scope.row.gym?.location }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="200">
          <template #default="scope">
            <div class="time-box">
              <el-icon class="me-1"><Clock /></el-icon>
              {{ formatDate(scope.row.bookingTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="150">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status, scope.row.attended)" effect="light" class="status-tag" round>
              <span class="dot"></span>
              {{ getStatusText(scope.row.status, scope.row.attended) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <div class="d-flex align-items-center gap-2">
              <el-button 
                v-if="scope.row.status === 'PENDING'" 
                type="danger" 
                link 
                class="cancel-btn"
                @click="cancelBooking(scope.row)"
              >
                取消预约
              </el-button>
              
              <el-button 
                v-if="scope.row.status === 'CONFIRMED' && !scope.row.attended" 
                type="success" 
                size="small"
                round
                @click="signIn(scope.row)"
              >
                签到入场
              </el-button>
              
              <el-button 
                v-if="scope.row.status !== 'PENDING'" 
                type="primary" 
                link 
                @click="$router.push(`/gym/${scope.row.gym.id}`)"
              >
                再次预约
              </el-button>
            </div>
          </template>
        </el-table-column>
        
        <template #empty>
          <div class="empty-state py-5">
            <el-empty description="暂无预约记录">
              <el-button type="primary" size="large" class="go-btn" @click="$router.push('/dashboard')" round>去发现好场馆</el-button>
            </el-empty>
          </div>
        </template>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }
.x-small { font-size: 12px; }

.header-glass {
  background: white;
  padding: 32px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.stat-pill {
  background: #f0f7ff;
  padding: 8px 20px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.stat-pill .lbl { color: #606266; font-size: 14px; }
.stat-pill .val { color: #409EFF; font-weight: 800; font-size: 18px; }

.refresh-btn {
  width: 46px;
  height: 46px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.table-container-modern {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0,0,0,0.03);
  border: 1px solid rgba(0,0,0,0.02);
}

.gym-mini-img {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gym-mini-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.time-box {
  color: #606266;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.status-tag {
  padding: 0 16px;
  height: 32px;
  line-height: 30px;
  font-weight: 600;
  border: none;
  display: inline-flex;
  align-items: center;
}

.status-tag .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 8px;
  background: currentColor;
}

.cancel-btn {
  font-weight: 600;
  transition: all 0.3s;
}

.cancel-btn:hover {
  color: #f56c6c !important;
  text-decoration: underline;
}

.go-btn {
  padding: 20px 40px !important;
  font-weight: 700 !important;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.3) !important;
}

:deep(.el-table__row) {
  transition: background-color 0.3s;
}

:deep(.el-table__row:hover) {
  background-color: #f8fbff !important;
}
</style>
