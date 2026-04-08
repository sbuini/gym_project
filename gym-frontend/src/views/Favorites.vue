<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const favorites = ref([])
const loading = ref(true)
const currentUser = ref(null)

const loadFavorites = async () => {
  const savedUser = localStorage.getItem('gym_user')
  if (!savedUser) {
    router.push('/login')
    return
  }
  currentUser.value = JSON.parse(savedUser)
  
  loading.value = true
  try {
    const res = await axios.get(`/api/users/${currentUser.value.id}/favorites`)
    favorites.value = res.data
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('加载收藏夹失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/gym/${id}`)
}

const removeFavorite = async (gymId) => {
  try {
    await axios.post(`/api/gyms/${gymId}/favorite`, null, {
      params: { userId: currentUser.value.id }
    })
    ElMessage.success('已从收藏夹移除')
    loadFavorites()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(loadFavorites)
</script>

<template>
  <div class="favorites-page" v-loading="loading">
    <div class="header-glass mb-5">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h2 class="fw-bold mb-1">我的收藏夹</h2>
          <div class="text-muted">保存您心仪的场馆，随时快速预约</div>
        </div>
        <div class="stat-pill">
          <span class="lbl">已收藏</span>
          <span class="val">{{ favorites.length }}</span>
        </div>
      </div>
    </div>

    <el-row :gutter="24" v-if="favorites.length > 0">
      <el-col :span="6" v-for="gym in favorites" :key="gym.id" class="mb-4">
        <div class="modern-gym-card">
          <div class="card-image-box small-height" @click="goToDetail(gym.id)">
            <img v-if="gym.imageUrl" :src="gym.imageUrl" class="card-img" />
            <div v-else class="card-placeholder">
              <el-icon size="32" color="#EBEEF5"><Picture /></el-icon>
            </div>
            <div class="card-category-badge" v-if="gym.category">{{ gym.category }}</div>
            <div class="remove-fav-overlay" @click.stop="removeFavorite(gym.id)">
              <el-icon><Delete /></el-icon>
            </div>
          </div>
          <div class="card-content p-3" @click="goToDetail(gym.id)">
            <h6 class="fw-bold text-dark mb-1 text-truncate">{{ gym.name }}</h6>
            <p class="text-muted x-small mb-3 text-truncate">{{ gym.location }}</p>
            <div class="d-flex justify-content-between align-items-center">
              <span class="fw-bold text-primary">¥{{ gym.price }}</span>
              <el-button type="primary" size="small" link>去预约 ></el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
    
    <el-empty v-else description="收藏夹还是空的呢">
      <el-button type="primary" size="large" round @click="router.push('/dashboard')">去发现好场馆</el-button>
    </el-empty>
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
  background: #fff0f0;
  padding: 8px 20px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #ffdbdb;
}

.stat-pill .lbl { color: #f56c6c; font-size: 14px; }
.stat-pill .val { color: #f56c6c; font-weight: 800; font-size: 18px; }

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

.modern-gym-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 30px 60px rgba(64, 158, 255, 0.12);
}

.card-image-box {
  height: 160px;
  position: relative;
  overflow: hidden;
}

.card-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
}

.remove-fav-overlay {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  background: rgba(245, 108, 108, 0.9);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s;
  backdrop-filter: blur(4px);
}

.modern-gym-card:hover .remove-fav-overlay {
  opacity: 1;
}

.remove-fav-overlay:hover {
  transform: scale(1.1);
  background: #f56c6c;
}
</style>
