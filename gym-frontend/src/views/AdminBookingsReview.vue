<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const status = ref('PENDING')

const loadPending = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/bookings', {
      params: status.value ? { status: status.value } : {}
    })
    list.value = res.data
  } catch (e) {
    console.error(e)
    ElMessage.error('加载待审核预约失败')
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const approve = async (row) => {
  try {
    await axios.post(`/api/admin/bookings/${row.id}/approve`)
    ElMessage.success('已通过审核')
    loadPending()
  } catch (e) {
    console.error(e)
    ElMessage.error('操作失败')
  }
}

const reject = async (row) => {
  ElMessageBox.confirm('确定拒绝该预约申请吗？', '审核确认', {
    confirmButtonText: '拒绝',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await axios.post(`/api/admin/bookings/${row.id}/reject`)
      ElMessage.success('已拒绝该预约')
      loadPending()
    } catch (e) {
      console.error(e)
      ElMessage.error('操作失败')
    }
  })
}

onMounted(loadPending)
</script>

<template>
  <div class="bookings-review" v-loading="loading">
    <div class="header-glass mb-5">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h2 class="fw-bold mb-1">预约申请审核</h2>
          <div class="text-muted">审批用户提交的场馆预约申请，确保资源合理分配</div>
        </div>
        <div class="d-flex align-items-center gap-3">
          <div class="stat-pill">
            <span class="lbl">待审核</span>
            <span class="val text-warning">{{ list.filter(i => i.status === 'PENDING').length }}</span>
          </div>
          <el-button type="primary" icon="Refresh" class="refresh-btn" @click="loadPending" circle></el-button>
        </div>
      </div>
    </div>

    <div class="table-card-modern">
      <el-table 
        :data="list" 
        style="width: 100%"
        size="large"
        :header-cell-style="{ background: '#f8fafc', fontWeight: '700', padding: '15px 0' }"
      >
        <el-table-column label="预约单信息" min-width="120">
          <template #default="scope">
            <span class="text-secondary small">#{{ scope.row.id }}</span>
          </template>
        </el-table-column>
        <el-table-column label="用户信息" min-width="180">
          <template #default="scope">
            <div class="d-flex align-items-center">
              <el-avatar :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="ms-3">
                <div class="fw-bold text-dark">{{ scope.row.user?.username }}</div>
                <div class="text-muted x-small">ID: {{ scope.row.user?.id }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="预约场馆" min-width="180">
          <template #default="scope">
            <div class="fw-bold">{{ scope.row.gym?.name }}</div>
            <div class="text-muted x-small">{{ scope.row.gym?.location }}</div>
          </template>
        </el-table-column>
        <el-table-column label="预约时间" width="180">
          <template #default="scope">
            <div class="d-flex align-items-center text-secondary">
              <el-icon class="me-1"><Clock /></el-icon>
              {{ formatDate(scope.row.bookingTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="130">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PENDING' ? 'warning' : (scope.row.status === 'CONFIRMED' ? 'success' : 'danger')" effect="light" class="status-tag" round>
              <span class="dot"></span>
              {{ scope.row.status === 'PENDING' ? '待审核' : (scope.row.status === 'CONFIRMED' ? '已确认' : '已取消') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核操作" width="220" fixed="right">
          <template #default="scope">
            <div v-if="scope.row.status === 'PENDING'" class="d-flex gap-2">
              <el-button 
                type="success" 
                size="small" 
                round 
                icon="Check" 
                @click="approve(scope.row)"
              >通过</el-button>
              <el-button 
                type="danger" 
                size="small" 
                round 
                icon="Close" 
                @click="reject(scope.row)"
              >驳回</el-button>
            </div>
            <span v-else class="text-muted small">已完成审核</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }
.x-small { font-size: 11px; }

.header-glass {
  background: white;
  padding: 30px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.stat-pill {
  background: #fffbe6;
  padding: 8px 20px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #ffe58f;
}

.stat-pill .lbl { color: #856404; font-size: 14px; }
.stat-pill .val { font-weight: 800; font-size: 18px; }

.refresh-btn {
  width: 46px;
  height: 46px;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.table-card-modern {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0,0,0,0.03);
}

.status-tag {
  padding: 0 16px;
  height: 28px;
  line-height: 26px;
  font-weight: 600;
  border: none;
  display: inline-flex;
  align-items: center;
}

.status-tag .dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  margin-right: 6px;
  background: currentColor;
}
</style>
