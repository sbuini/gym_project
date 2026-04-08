<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const user = ref(null)

const emailForm = ref({ email: '' })
const pwdForm = ref({ oldPassword: '', newPassword: '' })

const isValidEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

const load = async () => {
  const savedUser = localStorage.getItem('gym_user')
  if (!savedUser) {
    router.push('/login')
    return
  }
  const current = JSON.parse(savedUser)
  loading.value = true
  try {
    const res = await axios.get(`/api/users/${current.id}`)
    user.value = res.data
    emailForm.value.email = user.value?.email || ''
  } catch (e) {
    console.error(e)
    ElMessage.error('加载账号信息失败')
  } finally {
    loading.value = false
  }
}

const updateEmail = async () => {
  if (!user.value?.id) return
  if (!isValidEmail(emailForm.value.email)) {
    ElMessage.warning('请输入有效的邮箱格式')
    return
  }
  loading.value = true
  try {
    const res = await axios.put(`/api/users/${user.value.id}/email`, { email: emailForm.value.email })
    if (res.data?.success) {
      user.value = res.data.user
      localStorage.setItem('gym_user', JSON.stringify(res.data.user))
      ElMessage.success('邮箱已更新')
    } else {
      ElMessage.error('更新失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('更新邮箱失败')
  } finally {
    loading.value = false
  }
}

const changePassword = async () => {
  if (!user.value?.id) return
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    ElMessage.warning('请填写旧密码与新密码')
    return
  }
  loading.value = true
  try {
    const res = await axios.post(`/api/users/${user.value.id}/password`, pwdForm.value)
    if (res.data?.success) {
      ElMessage.success('密码修改成功')
      pwdForm.value.oldPassword = ''
      pwdForm.value.newPassword = ''
    } else {
      ElMessage.error(res.data?.message || '密码修改失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('密码修改失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="settings-page" v-loading="loading">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="fw-bold mb-1">账号设置</h3>
        <div class="text-muted">修改邮箱与密码（不支持头像）</div>
      </div>
      <el-button icon="ArrowLeft" round @click="$router.push('/profile')">返回</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="never" class="card">
          <div class="fw-bold mb-3">修改邮箱</div>
          <el-form :model="emailForm" label-position="top" size="large">
            <el-form-item label="邮箱">
              <el-input v-model="emailForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-button type="primary" round @click="updateEmail">保存邮箱</el-button>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="card">
          <div class="fw-bold mb-3">修改密码</div>
          <el-form :model="pwdForm" label-position="top" size="large">
            <el-form-item label="旧密码">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" />
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-button type="danger" round @click="changePassword">修改密码</el-button>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }
.settings-page {
  max-width: 1100px;
  margin: 0 auto;
}
.card {
  border-radius: 16px;
  border: none;
}
</style>
