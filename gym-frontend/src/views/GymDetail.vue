<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const gym = ref(null)
const currentUser = ref(null)
const isBooking = ref(false)
const loading = ref(true)
const isFavorited = ref(false)
const ratings = ref([])
const newRating = ref({
  score: 5,
  comment: ''
})
const showRatingDialog = ref(false)
const submittingRating = ref(false)

const fetchGym = async () => {
  loading.value = true
  try {
    const response = await axios.get(`/api/gyms/${route.params.id}`)
    gym.value = response.data
    
    const savedUser = localStorage.getItem('gym_user')
    if (savedUser) {
      currentUser.value = JSON.parse(savedUser)
      // 获取收藏状态和评价列表
      fetchExtraInfo()
    }
  } catch (error) {
    console.error('获取场馆详情失败:', error)
    ElMessage.error('无法加载场馆详情')
  } finally {
    loading.value = false
  }
}

const fetchExtraInfo = async () => {
  try {
    const [favRes, rateRes] = await Promise.all([
      axios.get(`/api/gyms/${gym.value.id}/favorite-status`, { params: { userId: currentUser.value.id } }),
      axios.get(`/api/gyms/${gym.value.id}/ratings`)
    ])
    isFavorited.value = favRes.data.favorited
    ratings.value = rateRes.data
  } catch (e) {
    console.error('获取额外信息失败:', e)
  }
}

const toggleFavorite = async () => {
  if (!currentUser.value) {
    ElMessage.warning('请先登录！')
    return
  }
  try {
    const res = await axios.post(`/api/gyms/${gym.value.id}/favorite`, null, {
      params: { userId: currentUser.value.id }
    })
    isFavorited.value = res.data.favorited
    ElMessage.success(isFavorited.value ? '已加入收藏夹' : '已取消收藏')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const submitRating = async () => {
  if (!newRating.value.comment) {
    ElMessage.warning('请输入评价内容')
    return
  }
  submittingRating.value = true
  try {
    await axios.post(`/api/gyms/${gym.value.id}/rate`, {
      userId: currentUser.value.id,
      score: newRating.value.score,
      comment: newRating.value.comment
    })
    ElMessage.success('感谢您的评价！')
    showRatingDialog.value = false
    newRating.value.comment = ''
    fetchExtraInfo()
  } catch (e) {
    ElMessage.error('评价提交失败')
  } finally {
    submittingRating.value = false
  }
}

const bookGym = async () => {
  if (!currentUser.value) {
    ElMessage.warning('请先登录！')
    router.push('/login')
    return
  }
  
  isBooking.value = true
  try {
    const response = await axios.post('/api/book', null, {
      params: {
        gymId: gym.value.id,
        userId: currentUser.value.id
      }
    })
    if (response.data.success) {
      ElMessage.success('预约申请已提交，等待管理员审核')
      setTimeout(() => {
        router.push('/bookings')
      }, 1500)
    } else {
      ElMessage.error(response.data.message || '预约失败')
    }
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error('预约失败，请重试')
  } finally {
    isBooking.value = false
  }
}

onMounted(fetchGym)
</script>

<template>
  <div class="gym-detail-container" v-loading="loading">
    <div class="header-section mb-5">
      <el-page-header title="返回列表" @back="router.push('/dashboard')" class="custom-header">
        <template #content>
          <span class="header-title">场馆详细信息</span>
        </template>
      </el-page-header>
    </div>

    <div v-if="gym" class="modern-detail-layout">
      <el-row :gutter="40">
        <el-col :md="12">
          <div class="image-gallery-box">
            <img v-if="gym.imageUrl" :src="gym.imageUrl" class="main-gym-image" />
            <div v-else class="image-placeholder-large">
              <el-icon size="80" color="#DCDFE6"><Picture /></el-icon>
            </div>
            <div class="glass-float-tag" v-if="gym.price < 40">
              <el-icon class="me-1"><TrendCharts /></el-icon>超值推荐
            </div>
          </div>
          
          <div class="features-card mt-4">
            <h5 class="fw-bold mb-4">场馆设施与服务</h5>
            <div class="features-grid">
              <div class="feature-item-modern"><el-icon class="icon"><CircleCheck /></el-icon> 设施齐全</div>
              <div class="feature-item-modern"><el-icon class="icon"><CircleCheck /></el-icon> 环境整洁</div>
              <div class="feature-item-modern"><el-icon class="icon"><CircleCheck /></el-icon> 专业指导</div>
              <div class="feature-item-modern"><el-icon class="icon"><CircleCheck /></el-icon> 交通便利</div>
              <div class="feature-item-modern"><el-icon class="icon"><CircleCheck /></el-icon> 免费 Wi-Fi</div>
              <div class="feature-item-modern"><el-icon class="icon"><CircleCheck /></el-icon> 淋浴间</div>
            </div>
          </div>
        </el-col>
        <el-col :md="12">
          <div class="info-glass-card">
            <div class="d-flex justify-content-between align-items-start mb-4">
              <div>
                <div class="d-flex align-items-center mb-2">
                  <h1 class="gym-name-title mb-0 me-3">{{ gym.name }}</h1>
                  <el-tag v-if="gym.category" effect="plain" round size="small">{{ gym.category }}</el-tag>
                </div>
                <div class="location-tag mb-2">
                  <el-icon class="me-2"><Location /></el-icon>{{ gym.location }}
                </div>
                <div class="phone-text small text-muted">
                  <el-icon class="me-1"><Phone /></el-icon>咨询电话：{{ gym.phone || '暂无' }}
                </div>
              </div>
              <div class="d-flex flex-column align-items-end">
                <div class="price-badge-large mb-3">
                  <span class="unit">¥</span>
                  <span class="num">{{ gym.price }}</span>
                  <span class="per">/小时</span>
                </div>
                <el-button 
                  :type="isFavorited ? 'warning' : 'info'" 
                  :icon="isFavorited ? 'StarFilled' : 'Star'" 
                  circle 
                  size="large"
                  class="fav-btn"
                  @click="toggleFavorite"
                ></el-button>
              </div>
            </div>

            <div class="description-section mb-4">
              <h5 class="fw-bold text-dark mb-3">场馆介绍</h5>
              <p class="desc-text">{{ gym.description || '该场馆暂无详细描述，欢迎致电咨询。' }}</p>
            </div>

            <!-- Ratings Section -->
            <div class="ratings-display mb-5">
              <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="fw-bold text-dark mb-0">用户评价 ({{ ratings.length }})</h5>
                <el-button type="primary" link icon="Edit" @click="showRatingDialog = true">写评价</el-button>
              </div>
              <div v-if="ratings.length > 0" class="rating-list">
                <div v-for="rate in ratings.slice(0, 3)" :key="rate.id" class="rating-item-mini">
                  <div class="d-flex justify-content-between mb-1">
                    <span class="user-name">{{ rate.user?.username }}</span>
                    <el-rate v-model="rate.score" disabled size="small" />
                  </div>
                  <p class="comment-text">{{ rate.comment }}</p>
                </div>
              </div>
              <el-empty v-else :image-size="60" description="暂无评价" />
            </div>

            <div class="booking-action-box">
              <div class="notice-card mb-4">
                <el-icon class="me-2"><InfoFilled /></el-icon>
                <span>预约后需等待管理员审核，确认后即可前往。</span>
              </div>
              <el-button 
                type="primary" 
                size="large" 
                class="booking-btn-huge w-100" 
                :loading="isBooking"
                @click="bookGym"
              >
                {{ isBooking ? '提交中...' : '立即预约该场馆' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Rating Dialog -->
    <el-dialog v-model="showRatingDialog" title="发表您的评价" width="400px" center round>
      <div class="text-center mb-4">
        <div class="mb-2">您对该场馆的满意度如何？</div>
        <el-rate v-model="newRating.score" size="large" />
      </div>
      <el-input
        v-model="newRating.comment"
        type="textarea"
        :rows="4"
        placeholder="请分享您的运动体验..."
      />
      <template #footer>
        <el-button @click="showRatingDialog = false" round>取消</el-button>
        <el-button type="primary" :loading="submittingRating" @click="submitRating" round>提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }

.header-section {
  background: white;
  padding: 20px 30px;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.02);
}

.header-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
}

.image-gallery-box {
  width: 100%;
  height: 450px;
  border-radius: 30px;
  overflow: hidden;
  position: relative;
  box-shadow: 0 20px 40px rgba(0,0,0,0.08);
}

.main-gym-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder-large {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.glass-float-tag {
  position: absolute;
  top: 24px;
  left: 24px;
  background: rgba(103, 194, 58, 0.85);
  backdrop-filter: blur(8px);
  color: white;
  padding: 8px 20px;
  border-radius: 14px;
  font-weight: 600;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.features-card {
  background: white;
  padding: 32px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.feature-item-modern {
  display: flex;
  align-items: center;
  color: #606266;
  font-size: 15px;
}

.feature-item-modern .icon {
  color: #67C23A;
  margin-right: 10px;
  font-size: 18px;
}

.info-glass-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 40px;
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 20px 50px rgba(0,0,0,0.04);
  height: 100%;
}

.gym-name-title {
  font-size: 32px;
  font-weight: 800;
  color: #1a1a1a;
  letter-spacing: -0.5px;
}

.location-tag {
  display: inline-flex;
  align-items: center;
  background: #f0f7ff;
  color: #409EFF;
  padding: 6px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
}

.price-badge-large {
  background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
  color: white;
  padding: 10px 20px;
  border-radius: 18px;
  box-shadow: 0 10px 20px rgba(255, 77, 79, 0.2);
}

.price-badge-large .unit { font-size: 14px; margin-right: 2px; }
.price-badge-large .num { font-size: 28px; font-weight: 800; line-height: 1; }
.price-badge-large .per { font-size: 12px; opacity: 0.8; }

.fav-btn {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: all 0.3s;
}

.fav-btn:hover {
  transform: scale(1.1);
}

.desc-text {
  color: #606266;
  line-height: 1.8;
  font-size: 15px;
}

.rating-item-mini {
  background: rgba(0,0,0,0.02);
  padding: 12px 16px;
  border-radius: 12px;
  margin-bottom: 10px;
}

.user-name { font-weight: 700; font-size: 13px; color: #303133; }
.comment-text { font-size: 13px; color: #606266; margin: 4px 0 0; }

.notice-card {
  background: #fffbe6;
  border: 1px solid #ffe58f;
  color: #856404;
  padding: 16px 20px;
  border-radius: 16px;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.booking-btn-huge {
  height: 64px !important;
  font-size: 18px !important;
  font-weight: 700 !important;
  border-radius: 18px !important;
  box-shadow: 0 12px 30px rgba(64, 158, 255, 0.3) !important;
  transition: all 0.3s !important;
}

.booking-btn-huge:hover {
  transform: translateY(-4px);
  box-shadow: 0 18px 40px rgba(64, 158, 255, 0.4) !important;
}
</style>
