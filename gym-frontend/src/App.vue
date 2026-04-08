<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const currentUser = ref(null)

const isLoginPage = computed(() => route.path === '/login' || route.path === '/register' || route.path === '/')

const isAdmin = computed(() => currentUser.value?.role === 'ROLE_ADMIN')

const appTitle = computed(() => (isAdmin.value ? '智慧体育馆后台' : '智慧体育馆'))

const handleUserCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
    return
  }
  if (command === 'account-settings') {
    router.push('/account-settings')
    return
  }
  if (command === 'logout') {
    logout()
  }
}

const logout = () => {
  localStorage.removeItem('gym_user')
  localStorage.removeItem('gym_token')
  currentUser.value = null
  router.push('/login')
}

const updateUserInfo = () => {
  const savedUser = localStorage.getItem('gym_user')
  if (savedUser) {
    currentUser.value = JSON.parse(savedUser)
  } else {
    currentUser.value = null
  }
}

onMounted(() => {
  updateUserInfo()
})

// 监听路由变化，实时更新用户信息（解决登录后不刷新不显示用户名的问题）
watch(() => route.path, () => {
  updateUserInfo()
})

const getBreadcrumbName = (name) => {
  const map = {
    'welcome': '欢迎页',
    'home': '控制台首页',
    'gym-detail': '场馆详情',
    'bookings': '我的预约',
    'favorites': '我的收藏',
    'profile': '个人信息',
    'account-settings': '账号设置',
    'admin-dashboard': '后台看板',
    'admin-bookings-review': '预约审核',
    'admin-gyms': '场馆管理',
    'admin-users': '用户管理'
  }
  return map[name] || name
}
</script>

<template>
  <el-container class="layout-container" v-if="!isLoginPage">
    <!-- Background Decor -->
    <div class="app-bg-decor app-blob-1"></div>
    <div class="app-bg-decor app-blob-2"></div>
    
    <!-- Sidebar -->
    <el-aside width="260px" class="sidebar">
      <div class="logo-container">
        <el-icon size="28" color="#409EFF"><Basketball /></el-icon>
        <span class="logo-text">{{ appTitle }}</span>
      </div>
      
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical"
        background-color="#1f2d3d"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <template v-if="!isAdmin">
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>控制台首页</span>
          </el-menu-item>
          
          <el-menu-item index="/bookings">
            <el-icon><Calendar /></el-icon>
            <span>我的预约单</span>
          </el-menu-item>
          
          <el-menu-item index="/favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏夹</span>
          </el-menu-item>
        </template>

        <template v-else>
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataBoard /></el-icon>
            <span>后台看板</span>
          </el-menu-item>

          <el-menu-item index="/admin/gyms">
            <el-icon><Basketball /></el-icon>
            <span>场馆管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>

          <el-menu-item index="/admin/bookings-review">
            <el-icon><Tickets /></el-icon>
            <span>预约审核</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- Header -->
      <el-header class="header shadow-sm">
        <div class="d-flex align-items-center justify-content-between h-100 w-100">
          <div class="breadcrumb">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item>管理系统</el-breadcrumb-item>
              <el-breadcrumb-item>{{ getBreadcrumbName(route.name) }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="user-info d-flex align-items-center">
            <el-dropdown trigger="click" @command="handleUserCommand">
              <div class="avatar-wrapper">
                <el-avatar :size="36" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
                <div class="ms-2 user-text">
                  <div class="username">{{ currentUser?.username }}</div>
                </div>
                <el-icon class="ms-2"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item icon="User" command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item icon="Setting" command="account-settings">账号设置</el-dropdown-item>
                  <el-dropdown-item divided icon="SwitchButton" command="logout">安全退出</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- Main Content -->
      <el-main class="main-content">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>

  <router-view v-else></router-view>
</template>

<style>
:root {
  --el-color-primary: #409EFF;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  background-color: #f5f7f9;
}

.layout-container {
  height: 100vh;
  position: relative;
  overflow: hidden;
  background-color: #f8fafc;
}

.app-bg-decor {
  position: absolute;
  filter: blur(120px);
  z-index: 0;
  border-radius: 50%;
  opacity: 0.12;
  pointer-events: none;
}

.app-blob-1 {
  width: 600px;
  height: 600px;
  background: #409EFF;
  top: -200px;
  right: -100px;
}

.app-blob-2 {
  width: 500px;
  height: 500px;
  background: #36cfc9;
  bottom: -100px;
  left: -100px;
}

.sidebar {
  background-color: #1a232e;
  box-shadow: 4px 0 15px rgba(0,0,0,0.05);
  z-index: 10;
  display: flex;
  flex-direction: column;
}

.logo-container {
  height: 72px;
  display: flex;
  align-items: center;
  padding: 0 24px;
  background-color: transparent;
  color: white;
  border-bottom: 1px solid rgba(255,255,255,0.05);
}

.logo-text {
  margin-left: 14px;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 1px;
  background: linear-gradient(to right, #fff, #409EFF);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.el-menu-vertical {
  border-right: none;
  background-color: transparent !important;
  flex: 1;
  padding-top: 12px;
}

.el-menu-item {
  height: 56px !important;
  line-height: 56px !important;
  margin: 4px 16px;
  border-radius: 12px;
  transition: all 0.3s;
}

.el-menu-item:hover {
  background-color: rgba(64, 158, 255, 0.1) !important;
  color: #409EFF !important;
}

.el-menu-item.is-active {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.2) 0%, rgba(64, 158, 255, 0.1) 100%) !important;
  color: #409EFF !important;
  font-weight: 600;
}

.header {
  background: rgba(255, 255, 255, 0.7) !important;
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  height: 72px !important;
  padding: 0 32px;
  z-index: 9;
}

.breadcrumb {
  font-weight: 500;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 30px;
  border: 1px solid rgba(0,0,0,0.03);
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-wrapper:hover {
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.username {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
}

.main-content {
  padding: 32px;
  z-index: 1;
  overflow-y: auto;
}

/* Global scrollbar styling */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

::-webkit-scrollbar-track {
  background: transparent;
}
</style>
