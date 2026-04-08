<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loginForm = ref({
  username: '',
  password: ''
})
const loading = ref(false)

const handleLogin = async () => {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/auth/login', loginForm.value)
    if (response.data.success) {
      localStorage.setItem('gym_user', JSON.stringify(response.data.user))
      localStorage.setItem('gym_token', response.data.token)
      ElMessage.success('登录成功，欢迎回来！')
      router.push(response.data.user?.role === 'ROLE_ADMIN' ? '/admin/dashboard' : '/dashboard')
    } else {
      ElMessage.error(response.data.message || '用户名或密码错误')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error('服务器连接失败，请稍后再试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="left-content">
          <div class="back-home-wrapper" @click="router.push('/')">
            <el-icon class="me-1"><ArrowLeft /></el-icon>
            <span class="small">返回欢迎页</span>
          </div>
          <el-icon size="64" color="#fff"><Basketball /></el-icon>
          <h1 class="text-white fw-bold mt-4">智慧体育馆</h1>
          <p class="text-white opacity-75">一站式智能场馆预约与管理平台</p>
          <div class="features mt-5">
            <div class="feature-item"><el-icon class="me-2"><Check /></el-icon> 实时场地预约</div>
            <div class="feature-item"><el-icon class="me-2"><Check /></el-icon> 智能个性化推荐</div>
            <div class="feature-item"><el-icon class="me-2"><Check /></el-icon> 便捷的后台管理</div>
          </div>
        </div>
      </div>
      <div class="login-right">
        <div class="login-box">
          <div class="text-center mb-5">
            <h2 class="fw-bold text-dark">账号登录</h2>
            <p class="text-muted small">请输入您的凭据以访问系统</p>
          </div>
          
          <el-form :model="loginForm" label-position="top" size="large">
            <el-form-item label="用户名">
              <el-input 
                v-model="loginForm.username" 
                placeholder="请输入用户名"
                prefix-icon="User"
              ></el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input 
                v-model="loginForm.password" 
                type="password" 
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              ></el-input>
            </el-form-item>
            
            <div class="d-flex justify-content-between align-items-center mb-4">
              <el-checkbox label="记住我"></el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
            
            <el-button 
              type="primary" 
              class="w-100 login-btn" 
              :loading="loading"
              @click="handleLogin"
            >
              立即登录
            </el-button>
            
            <div class="text-center mt-4">
              <span class="text-muted small">还没有账号？</span>
              <el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
            </div>
          </el-form>
          
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}

.login-container {
  display: flex;
  width: 1000px;
  height: 600px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0,0,0,0.1);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #409EFF 0%, #36cfc9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
}

.login-left::before {
  content: "";
  position: absolute;
  width: 100%;
  height: 100%;
  background-image: radial-gradient(circle at 20% 30%, rgba(255,255,255,0.1) 0%, transparent 50%);
}

.back-home-wrapper {
  position: absolute;
  top: 32px;
  left: 32px;
  display: flex;
  align-items: center;
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  padding: 10px 16px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s;
  z-index: 10;
}

.back-home-wrapper:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  transform: translateX(-4px);
}

.left-content {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.feature-item {
  color: white;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  font-size: 15px;
}

.login-right {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-box {
  width: 100%;
  max-width: 360px;
}

.fw-bold { font-weight: 700; }
.x-small { font-size: 12px; }

.login-btn {
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
}

:deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 15px;
}
</style>
