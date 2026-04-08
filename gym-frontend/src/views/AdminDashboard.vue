<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const stats = ref({
  gyms: 0,
  users: 0,
  bookingsTotal: 0,
  bookingsPending: 0
})

const pending = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try {
    const [statsRes, pendingRes] = await Promise.all([
      axios.get('/api/admin/stats'),
      axios.get('/api/admin/bookings/pending')
    ])
    stats.value = statsRes.data
    pending.value = pendingRes.data
  } catch (e) {
    console.error(e)
    ElMessage.error('加载后台数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <div class="admin-dashboard" v-loading="loading">
    <div class="header-row mb-5">
      <div>
        <h2 class="fw-bold mb-1">后台数据看板</h2>
        <div class="text-muted">实时掌握系统运行情况与预约审核进度</div>
      </div>
      <el-button type="primary" icon="Refresh" class="refresh-btn" @click="loadData" round>刷新数据</el-button>
    </div>

    <el-row :gutter="24" class="mb-5">
      <el-col :span="6">
        <div class="stat-glass-card">
          <div class="icon-box bg-blue"><el-icon><Basketball /></el-icon></div>
          <div class="info">
            <div class="lbl">场馆总数</div>
            <div class="val">{{ stats.gyms }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-glass-card">
          <div class="icon-box bg-purple"><el-icon><User /></el-icon></div>
          <div class="info">
            <div class="lbl">用户总数</div>
            <div class="val">{{ stats.users }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-glass-card">
          <div class="icon-box bg-orange"><el-icon><Tickets /></el-icon></div>
          <div class="info">
            <div class="lbl">预约总数</div>
            <div class="val">{{ stats.bookingsTotal }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-glass-card">
          <div class="icon-box bg-green"><el-icon><Clock /></el-icon></div>
          <div class="info">
            <div class="lbl">待审核</div>
            <div class="val">{{ stats.bookingsPending }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="table-card-modern">
      <div class="card-header-modern d-flex align-items-center justify-content-between">
        <div class="fw-bold fs-5">最新待审核预约</div>
        <el-button type="primary" link @click="$router.push('/admin/bookings-review')">
          进入预约审核 <el-icon class="ms-1"><ArrowRight /></el-icon>
        </el-button>
      </div>
      <el-table 
        :data="pending.slice(0, 8)" 
        style="width: 100%"
        size="large"
        :header-cell-style="{ background: '#f8fafc', fontWeight: '700', padding: '15px 0' }"
      >
        <el-table-column label="预约编号" prop="id" width="110" />
        <el-table-column label="用户信息" min-width="150">
          <template #default="scope">
            <div class="d-flex align-items-center">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="ms-2 fw-bold">{{ scope.row.user?.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="场馆" prop="gym.name" />
        <el-table-column label="申请时间" width="180">
          <template #default="scope">
            {{ new Date(scope.row.bookingTime).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default>
            <el-button type="primary" link @click="$router.push('/admin/bookings-review')">去审核</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  padding: 30px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.refresh-btn {
  padding: 12px 24px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.2);
}

.stat-glass-card {
  background: white;
  padding: 24px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
  border: 1px solid rgba(0,0,0,0.02);
  transition: transform 0.3s;
}

.stat-glass-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0,0,0,0.05);
}

.icon-box {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-right: 16px;
}

.bg-blue { background: linear-gradient(135deg, #409EFF 0%, #64b5f6 100%); }
.bg-purple { background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%); }
.bg-orange { background: linear-gradient(135deg, #fa8c16 0%, #ffd666 100%); }
.bg-green { background: linear-gradient(135deg, #52c41a 0%, #b7eb8f 100%); }

.lbl { color: #909399; font-size: 14px; margin-bottom: 4px; }
.val { font-size: 28px; font-weight: 800; color: #303133; line-height: 1; }

.table-card-modern {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0,0,0,0.03);
}

.card-header-modern {
  padding: 24px 32px;
  border-bottom: 1px solid #f0f2f5;
}
</style>
