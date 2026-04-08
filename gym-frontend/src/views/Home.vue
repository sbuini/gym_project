<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const gyms = ref([])
const recommendations = ref([])
const recentBookings = ref([])
const userStats = ref({
  totalBookings: 0,
  completedBookings: 0,
  totalSpent: 0,
  balance: 0
})
const currentUser = ref(null)
const loading = ref(true)

const news = [
  { id: 1, title: '新场馆开业：奥体中心游泳馆正式上线', date: '2024-03-28' },
  { id: 2, title: '春季健身狂欢节：充值最高送500元', date: '2024-03-25' },
  { id: 3, title: '系统升级公告：新增收藏夹与评价系统', date: '2024-03-20' }
]

// 搜索和筛选状态
const searchQuery = ref('')
const selectedCategory = ref('全部')
const sortBy = ref('default')

const categories = ['全部', '健身', '瑜伽', '游泳', '搏击', '骑行', '球类', '攀岩', '竞技', '综合']

const filteredGyms = computed(() => {
  let result = [...gyms.value]
  
  // 搜索过滤
  if (searchQuery.value) {
    result = result.filter(g => 
      g.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      g.location.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  
  // 分类过滤
  if (selectedCategory.value !== '全部') {
    result = result.filter(g => g.category === selectedCategory.value)
  }
  
  // 排序
  if (sortBy.value === 'price-asc') {
    result.sort((a, b) => a.price - b.price)
  } else if (sortBy.value === 'price-desc') {
    result.sort((a, b) => b.price - a.price)
  }
  
  return result
})

const fetchData = async () => {
  loading.value = true
  try {
    const gymsRes = await axios.get('/api/gyms')
    gyms.value = gymsRes.data

    const savedUser = localStorage.getItem('gym_user')
    if (savedUser) {
      currentUser.value = JSON.parse(savedUser)
      const [recsRes, statsRes, bookingsRes] = await Promise.all([
        axios.get(`/api/recommendations/${currentUser.value.id}`),
        axios.get(`/api/users/${currentUser.value.id}/stats`),
        axios.get(`/api/bookings/${currentUser.value.id}`)
      ])
      recommendations.value = recsRes.data
      userStats.value = statsRes.data
      recentBookings.value = bookingsRes.data.slice(0, 3) // 只显示最近3个
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/gym/${id}`)
}

onMounted(fetchData)
</script>

<template>
  <div class="dashboard" v-loading="loading">
    <el-row :gutter="24">
      <el-col :span="24">
        <div class="welcome-banner mb-5 p-5 rounded-5 shadow-sm d-flex align-items-center position-relative overflow-hidden">
          <div class="banner-decor"></div>
          <div class="flex-grow-1 position-relative" style="z-index: 1">
            <h1 class="fw-bold text-white mb-2">您好, {{ currentUser?.username }}!</h1>
            <p class="text-white opacity-75 fs-5 mb-0">欢迎使用智慧体育馆管理系统。今天想去哪里锻炼？</p>
          </div>
          <el-button type="warning" size="large" icon="Calendar" class="banner-btn" @click="router.push('/bookings')" round>我的预约日程</el-button>
        </div>
      </el-col>
    </el-row>

    <!-- Statistics Cards -->
    <el-row :gutter="24" class="mb-5">
      <el-col :span="6">
        <div class="glass-stat-card wallet-card" @click="router.push('/profile')">
          <div class="stat-icon bg-orange-gradient"><el-icon><Wallet /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">我的余额</div>
            <div class="stat-value">¥{{ userStats.balance.toFixed(2) }}</div>
          </div>
          <div class="card-action-hint">去充值 ></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="glass-stat-card">
          <div class="stat-icon bg-primary-gradient"><el-icon><Basketball /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">开放场馆</div>
            <div class="stat-value">{{ gyms.length }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="glass-stat-card">
          <div class="stat-icon bg-success-gradient"><el-icon><Star /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">智能推荐</div>
            <div class="stat-value">{{ recommendations.length }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="glass-stat-card" @click="router.push('/bookings')">
          <div class="stat-icon bg-purple-gradient"><el-icon><Calendar /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">待参加预约</div>
            <div class="stat-value">{{ recentBookings.filter(b => b.status === 'CONFIRMED' && !b.attended).length }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="mb-5">
      <!-- Recent Bookings Widget -->
      <el-col :span="16">
        <div class="glass-panel p-4 h-100">
          <div class="d-flex justify-content-between align-items-center mb-4">
            <h4 class="fw-bold mb-0">最近预约日程</h4>
            <el-button link type="primary" @click="router.push('/bookings')">查看全部</el-button>
          </div>
          <div v-if="recentBookings.length > 0" class="booking-list">
            <div v-for="booking in recentBookings" :key="booking.id" class="booking-item">
              <div class="booking-gym-info">
                <div class="booking-gym-name">{{ booking.gym.name }}</div>
                <div class="booking-time text-muted small">{{ booking.bookingTime }}</div>
              </div>
              <div class="booking-status">
                <el-tag :type="booking.attended ? 'info' : (booking.status === 'CONFIRMED' ? 'success' : (booking.status === 'PENDING' ? 'warning' : 'info'))" round size="small">
                  {{ booking.attended ? '已签到' : (booking.status === 'CONFIRMED' ? '待签到' : (booking.status === 'PENDING' ? '待审核' : '已取消')) }}
                </el-tag>
              </div>
            </div>
          </div>
          <el-empty v-else :image-size="60" description="暂无预约日程" />
        </div>
      </el-col>

      <!-- News Widget -->
      <el-col :span="8">
        <div class="glass-panel p-4 h-100">
          <h4 class="fw-bold mb-4">公告动态</h4>
          <div class="news-list">
            <div v-for="item in news" :key="item.id" class="news-item">
              <div class="news-dot"></div>
              <div class="news-content">
                <div class="news-title text-truncate">{{ item.title }}</div>
                <div class="news-date text-muted">{{ item.date }}</div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Recommendations Section -->
    <div v-if="recommendations.length > 0" class="mb-5">
      <div class="d-flex align-items-center justify-content-between mb-4">
        <div class="d-flex align-items-center">
          <h3 class="fw-bold mb-0">猜你喜欢</h3>
          <div class="ai-tag ms-3">AI 智能推荐</div>
        </div>
      </div>
      <el-row :gutter="24">
        <el-col :span="8" v-for="gym in recommendations" :key="gym.id">
          <div class="modern-gym-card recommendation-border" @click="goToDetail(gym.id)">
            <div class="card-image-box">
              <img v-if="gym.imageUrl" :src="gym.imageUrl" class="card-img" />
              <div v-else class="card-placeholder">
                <el-icon size="48" color="#DCDFE6"><Picture /></el-icon>
              </div>
              <div class="card-badge">推荐</div>
            </div>
            <div class="card-content">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="fw-bold text-dark mb-0">{{ gym.name }}</h5>
                <div class="card-price">¥{{ gym.price }}<small>/h</small></div>
              </div>
              <p class="text-muted small mb-4"><el-icon class="me-1"><Location /></el-icon>{{ gym.location }}</p>
              <div class="card-footer-btn">查看详情</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- All Gyms Section -->
    <div>
      <div class="d-flex align-items-center justify-content-between mb-4">
        <h3 class="fw-bold mb-0">全部场馆资源</h3>
        <div class="filter-controls d-flex gap-3">
          <el-input
            v-model="searchQuery"
            placeholder="搜索场馆或位置..."
            prefix-icon="Search"
            style="width: 250px"
            clearable
          />
          <el-select v-model="selectedCategory" style="width: 120px">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
          <el-select v-model="sortBy" style="width: 150px">
            <el-option label="默认排序" value="default" />
            <el-option label="价格: 从低到高" value="price-asc" />
            <el-option label="价格: 从高到低" value="price-desc" />
          </el-select>
        </div>
      </div>
      
      <el-row :gutter="24" v-if="filteredGyms.length > 0">
        <el-col :span="6" v-for="gym in filteredGyms" :key="gym.id" class="mb-4">
          <div class="modern-gym-card" @click="goToDetail(gym.id)">
            <div class="card-image-box small-height">
              <img v-if="gym.imageUrl" :src="gym.imageUrl" class="card-img" />
              <div v-else class="card-placeholder">
                <el-icon size="32" color="#EBEEF5"><Picture /></el-icon>
              </div>
              <div class="card-category-badge" v-if="gym.category">{{ gym.category }}</div>
            </div>
            <div class="card-content p-3">
              <h6 class="fw-bold text-dark mb-1 text-truncate">{{ gym.name }}</h6>
              <p class="text-muted x-small mb-3 text-truncate">{{ gym.location }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <span class="fw-bold text-primary">¥{{ gym.price }}</span>
                <span class="text-primary x-small fw-bold">详情 ></span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-empty v-else description="没有找到符合条件的场馆" />
    </div>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }
.x-small { font-size: 11px; }

.welcome-banner {
  background: linear-gradient(135deg, #409EFF 0%, #36cfc9 100%);
  border: none;
  min-height: 200px;
}

.banner-decor {
  position: absolute;
  width: 300px;
  height: 300px;
  background: rgba(255,255,255,0.1);
  border-radius: 50%;
  top: -150px;
  right: -50px;
  z-index: 0;
  pointer-events: none;
}

.banner-btn {
  padding: 25px 35px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
  border: none;
  position: relative;
  z-index: 2;
}

.glass-stat-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  padding: 24px;
  border-radius: 24px;
  display: flex;  align-items: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.03);
  transition: transform 0.3s;
}

.glass-stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
  margin-right: 20px;
}

.bg-primary-gradient { background: linear-gradient(135deg, #409EFF 0%, #64b5f6 100%); }
.bg-success-gradient { background: linear-gradient(135deg, #67C23A 0%, #95de64 100%); }
.bg-orange-gradient { background: linear-gradient(135deg, #fa8c16 0%, #ffd666 100%); }
.bg-purple-gradient { background: linear-gradient(135deg, #722ed1 0%, #b37feb 100%); }

.wallet-card {
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.wallet-card:hover {
  border-color: #fa8c16;
}

.card-action-hint {
  position: absolute;
  right: 24px;
  bottom: 24px;
  font-size: 12px;
  color: #fa8c16;
  font-weight: 600;
  opacity: 0;
  transform: translateX(10px);
  transition: all 0.3s;
}

.wallet-card:hover .card-action-hint {
  opacity: 1;
  transform: translateX(0);
}

.glass-panel {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 28px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.02);
}

.booking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: white;
  border-radius: 16px;
  margin-bottom: 12px;
  border: 1px solid rgba(0,0,0,0.02);
  transition: all 0.3s;
}

.booking-item:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.booking-gym-name {
  font-weight: 700;
  color: #303133;
  margin-bottom: 2px;
}

.news-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(0,0,0,0.03);
}

.news-item:last-child {
  border-bottom: none;
}

.news-dot {
  width: 8px;
  height: 8px;
  background: #409EFF;
  border-radius: 50%;
  margin-right: 16px;
}

.news-content {
  flex: 1;
  overflow: hidden;
}

.news-title {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  margin-bottom: 2px;
}

.news-date {
  font-size: 12px;
}

.stat-label {
  color: #909399;
  font-size: 14px;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 800;
  color: #303133;
  line-height: 1;
}

.ai-tag {
  background: linear-gradient(135deg, #67C23A 0%, #95de64 100%);
  color: white;
  padding: 4px 16px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.modern-gym-card {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
  border: 1px solid rgba(0,0,0,0.03);
  position: relative;
}

.recommendation-border {
  border: 2px solid rgba(103, 194, 58, 0.1);
}

.modern-gym-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 30px 60px rgba(64, 158, 255, 0.12);
}

.card-image-box {
  height: 220px;
  position: relative;
  overflow: hidden;
}

.small-height {
  height: 160px;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s;
}

.modern-gym-card:hover .card-img {
  transform: scale(1.1);
}

.card-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(103, 194, 58, 0.9);
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  backdrop-filter: blur(4px);
}

.card-category-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255, 255, 255, 0.9);
  color: #409EFF;
  padding: 2px 10px;
  border-radius: 8px;
  font-size: 11px;
  font-weight: 700;
  backdrop-filter: blur(4px);
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
}

.card-price {
  font-size: 20px;
  font-weight: 800;
  color: #409EFF;
}

.card-price small {
  font-size: 12px;
  color: #909399;
  font-weight: 400;
}

.card-content {
  padding: 24px;
}

.card-footer-btn {
  background: #f0f7ff;
  color: #409EFF;
  text-align: center;
  padding: 12px;
  border-radius: 16px;
  font-weight: 600;
  transition: all 0.3s;
}

.modern-gym-card:hover .card-footer-btn {
  background: #409EFF;
  color: white;
}
</style>
