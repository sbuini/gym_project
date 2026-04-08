import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Welcome from '../views/Welcome.vue'
import GymDetail from '../views/GymDetail.vue'
import Bookings from '../views/Bookings.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AdminGyms from '../views/AdminGyms.vue'
import AdminUsers from '../views/AdminUsers.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import AdminBookingsReview from '../views/AdminBookingsReview.vue'
import Profile from '../views/Profile.vue'
import AccountSettings from '../views/AccountSettings.vue'
import Favorites from '../views/Favorites.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login,
      meta: { requiresAuth: false, title: '账号登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: Register,
      meta: { requiresAuth: false, title: '账号注册' }
    },
    {
      path: '/',
      name: 'welcome',
      component: Welcome,
      meta: { requiresAuth: false, title: '欢迎' }
    },
    {
      path: '/dashboard',
      name: 'home',
      component: Home,
      meta: { requiresAuth: true, title: '控制台' }
    },
    {
      path: '/gym/:id',
      name: 'gym-detail',
      component: GymDetail,
      meta: { requiresAuth: true, title: '场馆详情' }
    },
    {
      path: '/bookings',
      name: 'bookings',
      component: Bookings,
      meta: { requiresAuth: true, title: '我的预约' }
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: Favorites,
      meta: { requiresAuth: true, title: '我的收藏' }
    },
    {
      path: '/profile',
      name: 'profile',
      component: Profile,
      meta: { requiresAuth: true, title: '个人信息' }
    },
    {
      path: '/account-settings',
      name: 'account-settings',
      component: AccountSettings,
      meta: { requiresAuth: true, title: '账号设置' }
    },
    {
      path: '/admin/gyms',
      name: 'admin-gyms',
      component: AdminGyms,
      meta: { requiresAuth: true, requiresAdmin: true, title: '场馆管理' }
    },
    {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: AdminDashboard,
      meta: { requiresAuth: true, requiresAdmin: true, title: '管理面板' }
    },
    {
      path: '/admin/users',
      name: 'admin-users',
      component: AdminUsers,
      meta: { requiresAuth: true, requiresAdmin: true, title: '用户管理' }
    },
    {
      path: '/admin/bookings-review',
      name: 'admin-bookings-review',
      component: AdminBookingsReview,
      meta: { requiresAuth: true, requiresAdmin: true, title: '预约审核' }
    }
  ]
})

// Navigation Guard
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} | 智慧体育馆`
  } else {
    document.title = '智慧体育馆'
  }

  const token = localStorage.getItem('gym_token')
  const userStr = localStorage.getItem('gym_user')
  const user = userStr ? JSON.parse(userStr) : null

  if (to.meta.requiresAuth && (!token || !user)) {
    next('/login')
    return
  }

  if ((to.path === '/login' || to.path === '/register' || to.path === '/') && token && user) {
    next(user?.role === 'ROLE_ADMIN' ? '/admin/dashboard' : '/dashboard')
    return
  }

  if (to.path === '/dashboard' && user?.role === 'ROLE_ADMIN') {
    next('/admin/dashboard')
    return
  }

  if (to.meta.requiresAdmin && user?.role !== 'ROLE_ADMIN') {
    next('/dashboard')
    return
  }

  next()
})

export default router
