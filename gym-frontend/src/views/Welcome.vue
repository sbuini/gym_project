<script setup>
import { useRouter } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'

const router = useRouter()
const isLoggedIn = ref(false)
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  isLoggedIn.value = !!localStorage.getItem('gym_token')
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

const startNow = () => {
  if (isLoggedIn.value) {
    router.push('/dashboard')
  } else {
    router.push('/login')
  }
}

const scrollToFeatures = () => {
  const featuresSection = document.querySelector('.features')
  featuresSection.scrollIntoView({ behavior: 'smooth' })
}
</script>

<template>
  <div class="welcome-container">
    <!-- Background Decor -->
    <div class="bg-decor blob-1"></div>
    <div class="bg-decor blob-2"></div>
    
    <!-- Navbar -->
    <nav class="navbar" :class="{ 'navbar-scrolled': isScrolled }">
      <div class="nav-content">
        <div class="logo" @click="router.push('/')">
          <div class="logo-icon">
            <el-icon size="24" color="#fff"><Basketball /></el-icon>
          </div>
          <span class="logo-text">智慧体育馆</span>
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <header class="hero">
      <div class="hero-content">
        <h1 class="animate-up">开启您的<span class="highlight">智慧运动</span>生活</h1>
        <p class="hero-subtitle animate-up-delay-1">一站式智能场馆预约与管理平台，让运动更简单，让健康更智能。</p>
        <div class="cta-group animate-up-delay-2">
          <el-button type="primary" size="large" round class="cta-btn" @click="startNow">
            立即开始
            <el-icon class="ms-2"><ArrowRight /></el-icon>
          </el-button>
          <el-button size="large" round class="cta-btn-secondary" @click="scrollToFeatures">了解更多</el-button>
        </div>
      </div>
      <div class="hero-visual animate-fade">
        <div class="visual-bg-circle"></div>
        <div class="glass-card main-card">
          <div class="card-header">
            <div class="header-icon"><el-icon><Monitor /></el-icon></div>
            <div class="header-text">
              <span class="title">今日客流分布</span>
              <span class="subtitle">实时监控中...</span>
            </div>
          </div>
          <div class="chart-mock">
            <div class="bar" style="height: 40%"></div>
            <div class="bar" style="height: 70%"></div>
            <div class="bar active" style="height: 100%"></div>
            <div class="bar" style="height: 60%"></div>
            <div class="bar" style="height: 85%"></div>
            <div class="bar" style="height: 50%"></div>
          </div>
          <div class="card-footer">
            <div class="stat-item">
              <span class="val">85%</span>
              <span class="lbl">利用率</span>
            </div>
            <div class="stat-item">
              <span class="val">12</span>
              <span class="lbl">待处理</span>
            </div>
          </div>
        </div>
        <div class="glass-card small-card top-right">
          <div class="icon-circle bg-success"><el-icon><Check /></el-icon></div>
          <div class="ms-2">
            <div class="fw-bold small">预约成功</div>
            <div class="text-muted x-small">篮球馆 14:00</div>
          </div>
        </div>
        <div class="glass-card small-card bottom-left">
          <div class="icon-circle bg-warning"><el-icon><Star /></el-icon></div>
          <div class="ms-2">
            <div class="fw-bold small">智能推荐</div>
            <div class="text-muted x-small">根据您的运动偏好</div>
          </div>
        </div>
      </div>
    </header>

    <!-- Features Section -->
    <section class="features">
      <h2 class="section-title">核心优势</h2>
      <div class="feature-grid">
        <div class="feature-card">
          <div class="feature-icon bg-blue">
            <el-icon><Calendar /></el-icon>
          </div>
          <h3>便捷预约</h3>
          <p>随时随地查看场地占用情况，一键预约心仪场馆。</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon bg-green">
            <el-icon><MagicStick /></el-icon>
          </div>
          <h3>智能推荐</h3>
          <p>基于 AI 算法，根据您的运动习惯提供个性化场馆建议。</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon bg-orange">
            <el-icon><DataLine /></el-icon>
          </div>
          <h3>数据统计</h3>
          <p>记录您的运动足迹，分析运动频率与偏好，掌握健康脉搏。</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon bg-purple">
            <el-icon><Monitor /></el-icon>
          </div>
          <h3>多端同步</h3>
          <p>移动端与 PC 端无缝衔接，随时掌握最新的预约动态。</p>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <p>&copy; 2024 智慧体育馆管理系统. 版权所有.</p>
    </footer>
  </div>
</template>

<style scoped>
.welcome-container {
  min-height: 100vh;
  background-color: #fff;
  background-image: 
    radial-gradient(at 0% 0%, rgba(64, 158, 255, 0.05) 0, transparent 50%),
    radial-gradient(at 100% 100%, rgba(54, 207, 201, 0.05) 0, transparent 50%);
  color: #2c3e50;
  overflow-x: hidden;
  position: relative;
}

.bg-decor {
  position: absolute;
  filter: blur(100px);
  z-index: 0;
  border-radius: 50%;
  opacity: 0.15;
}

.blob-1 {
  width: 500px;
  height: 500px;
  background: #409EFF;
  top: -100px;
  right: -100px;
}

.blob-2 {
  width: 400px;
  height: 400px;
  background: #36cfc9;
  bottom: 20%;
  left: -100px;
}

/* Navbar */
.navbar {
  padding: 24px 80px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: transparent;
  z-index: 1000;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
}

.navbar-scrolled {
  padding: 12px 80px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
}

.nav-content {
  max-width: 1280px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.3s;
}

.logo:hover {
  transform: scale(1.02);
}

.logo-icon {
  width: 42px;
  height: 42px;
  background: linear-gradient(135deg, #409EFF 0%, #36cfc9 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 16px rgba(64, 158, 255, 0.3);
}

.logo-text {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #303133 0%, #606266 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 24px;
}

.nav-btn-link {
  font-size: 15px;
  font-weight: 600;
  color: #606266 !important;
  transition: color 0.3s;
}

.nav-btn-link:hover {
  color: #409EFF !important;
}

.nav-btn-primary {
  padding: 10px 24px !important;
  font-weight: 600;
  font-size: 15px;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.2);
  border: none;
}

.nav-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 24px rgba(64, 158, 255, 0.3);
}

/* Hero Section */
.hero {
  padding: 160px 80px 100px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 60px;
}

.hero-content {
  flex: 1;
}

.hero h1 {
  font-size: 56px;
  line-height: 1.2;
  font-weight: 800;
  margin-bottom: 24px;
}

.highlight {
  color: #409EFF;
}

.hero-subtitle {
  font-size: 20px;
  color: #606266;
  margin-bottom: 40px;
  line-height: 1.6;
}

.cta-group {
  display: flex;
  gap: 20px;
}

.cta-btn {
  padding: 25px 40px;
  font-size: 18px;
  font-weight: 600;
}

.cta-btn-secondary {
  padding: 25px 40px;
  font-size: 18px;
  font-weight: 600;
}

/* Hero Visual */
.hero-visual {
  flex: 1;
  position: relative;
  height: 450px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.visual-bg-circle {
  position: absolute;
  width: 380px;
  height: 380px;
  background: linear-gradient(135deg, #409EFF 0%, #36cfc9 100%);
  filter: blur(80px);
  opacity: 0.2;
  border-radius: 50%;
  z-index: 0;
}

.glass-card {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  box-shadow: 0 20px 50px rgba(0,0,0,0.08);
  position: relative;
  z-index: 1;
  transition: transform 0.3s;
}

.glass-card:hover {
  transform: translateY(-5px);
}

.main-card {
  width: 320px;
  padding: 24px;
}

.small-card {
  position: absolute;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.top-right {
  top: 40px;
  right: -20px;
  animation: float 4s ease-in-out infinite;
}

.bottom-left {
  bottom: 40px;
  left: -20px;
  animation: float 4s ease-in-out infinite 1s;
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.header-icon {
  width: 40px;
  height: 40px;
  background: #409EFF;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 12px;
}

.header-text {
  display: flex;
  flex-direction: column;
}

.header-text .title {
  font-weight: 700;
  font-size: 15px;
}

.header-text .subtitle {
  font-size: 11px;
  color: #909399;
}

.chart-mock {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  height: 120px;
  margin-bottom: 24px;
  padding: 0 10px;
}

.bar {
  width: 25px;
  background: #E4E7ED;
  border-radius: 6px;
  transition: all 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.bar.active {
  background: #409EFF;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.card-footer {
  display: flex;
  border-top: 1px solid #EBEEF5;
  padding-top: 20px;
  justify-content: space-between;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-item .val {
  font-weight: 800;
  font-size: 18px;
  color: #303133;
}

.stat-item .lbl {
  font-size: 11px;
  color: #909399;
}

.icon-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.bg-success { background-color: #67C23A; }
.bg-warning { background-color: #E6A23C; }

.ms-2 { margin-left: 8px; }
.fw-bold { font-weight: 700; }
.small { font-size: 13px; }
.text-muted { color: #909399; }
.x-small { font-size: 11px; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

/* Features Section */
.features {
  padding: 100px 80px;
  background-color: #f8f9fa;
}

.section-title {
  text-align: center;
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 60px;
}

.feature-grid {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(5px);
  padding: 40px;
  border-radius: 24px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
  border: 1px solid rgba(0,0,0,0.03);
}

.feature-card:hover {
  transform: translateY(-12px);
  box-shadow: 0 30px 60px rgba(64, 158, 255, 0.1);
  border-color: rgba(64, 158, 255, 0.1);
}

.feature-icon {
  width: 70px;
  height: 70px;
  border-radius: 20px;
  margin: 0 auto 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: #fff;
}

.bg-blue { background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%); }
.bg-green { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); }
.bg-orange { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.bg-purple { background: linear-gradient(135deg, #8e2de2 0%, #4a00e0 100%); }

.feature-card h3 {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 16px;
}

.feature-card p {
  color: #909399;
  line-height: 1.6;
}

/* Footer */
.footer {
  padding: 40px;
  text-align: center;
  color: #909399;
  border-top: 1px solid #ebeef5;
}

/* Animations */
.animate-up {
  animation: slideUp 0.8s ease-out forwards;
}

.animate-up-delay-1 {
  opacity: 0;
  animation: slideUp 0.8s ease-out 0.2s forwards;
}

.animate-up-delay-2 {
  opacity: 0;
  animation: slideUp 0.8s ease-out 0.4s forwards;
}

.animate-fade {
  opacity: 0;
  animation: fadeIn 1.2s ease-out 0.6s forwards;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 1024px) {
  .hero {
    flex-direction: column;
    text-align: center;
    padding-top: 120px;
  }
  .cta-group {
    justify-content: center;
  }
  .navbar {
    padding: 20px 40px;
  }
}
</style>
