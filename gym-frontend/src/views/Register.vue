<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})
const loading = ref(false)

const isValidEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

const handleRegister = async () => {
  if (!registerForm.value.username || !registerForm.value.password || !registerForm.value.email) {
    ElMessage.warning('请填写所有必填字段')
    return
  }
  
  if (!isValidEmail(registerForm.value.email)) {
    ElMessage.warning('请输入有效的邮箱格式')
    return
  }
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    const response = await axios.post('/api/auth/register', {
      username: registerForm.value.username,
      password: registerForm.value.password,
      email: registerForm.value.email
    })
    
    if (response.data.success) {
      ElMessage.success('注册成功，请登录！')
      router.push('/login')
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('注册错误:', error)
    ElMessage.error('服务器连接失败，请稍后再试')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
        <div class="left-content">
          <div class="back-home-wrapper" @click="router.push('/')">
            <el-icon class="me-1"><ArrowLeft /></el-icon>
            <span class="small">返回欢迎页</span>
          </div>
          <el-icon size="64" color="#fff"><Basketball /></el-icon>
          <h1 class="text-white fw-bold mt-4">加入我们</h1>
          <p class="text-white opacity-75">开启您的健康运动之旅</p>
          <div class="features mt-5">
            <div class="feature-item"><el-icon class="me-2"><Check /></el-icon> 探索顶级运动场馆</div>
            <div class="feature-item"><el-icon class="me-2"><Check /></el-icon> 随时随地轻松预约</div>
            <div class="feature-item"><el-icon class="me-2"><Check /></el-icon> 享受个性化运动建议</div>
          </div>
        </div>
      </div>
      <div class="register-right">
        <div class="register-box">
          <div class="text-center mb-4">
            <h2 class="fw-bold text-dark">创建账号</h2>
            <p class="text-muted small">填写以下信息完成注册</p>
          </div>
          
          <el-form :model="registerForm" label-position="top" size="large">
            <el-form-item label="用户名" required>
              <el-input 
                v-model="registerForm.username" 
                placeholder="请输入用户名"
                prefix-icon="User"
              ></el-input>
            </el-form-item>
            <el-form-item label="邮箱" required>
              <el-input 
                v-model="registerForm.email" 
                placeholder="请输入邮箱"
                prefix-icon="Message"
              ></el-input>
            </el-form-item>
            <el-form-item label="密码" required>
              <el-input 
                v-model="registerForm.password" 
                type="password" 
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
              ></el-input>
            </el-form-item>
            <el-form-item label="确认密码" required>
              <el-input 
                v-model="registerForm.confirmPassword" 
                type="password" 
                placeholder="请再次输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleRegister"
              ></el-input>
            </el-form-item>
            
            <el-button 
              type="primary" 
              class="w-100 register-btn mt-3" 
              :loading="loading"
              @click="handleRegister"
            >
              立即注册
            </el-button>
            
            <div class="text-center mt-4">
              <span class="text-muted small">已有账号？</span>
              <el-link type="primary" :underline="false" @click="router.push('/login')">立即登录</el-link>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}

.register-container {
  display: flex;
  width: 1000px;
  height: 650px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 32px;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0,0,0,0.1);
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, #409EFF 0%, #36cfc9 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  position: relative;
}

.register-left::before {
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

.register-right {
  flex: 1.2;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-box {
  width: 100%;
  max-width: 360px;
}

.fw-bold { font-weight: 700; }

.register-btn {
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
