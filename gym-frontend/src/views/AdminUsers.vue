<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(true)

const loadUsers = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/users')
    users.value = res.data
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteUser = (user) => {
  if (user.role === 'ROLE_ADMIN') {
    ElMessage.warning('不能删除管理员账号')
    return
  }
  
  ElMessageBox.confirm(
    `确定要删除用户 "${user.username}" 吗？此操作不可恢复。`,
    '警告',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning',
      round: true
    }
  ).then(async () => {
    try {
      await axios.delete(`/api/admin/users/${user.id}`)
      ElMessage.success('用户已成功删除')
      loadUsers()
    } catch (error) {
      console.error('删除用户失败:', error)
      ElMessage.error('删除用户失败')
    }
  })
}

onMounted(loadUsers)
</script>

<template>
  <div class="admin-users" v-loading="loading">
    <div class="header-glass mb-5">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h2 class="fw-bold mb-1">系统用户管理</h2>
          <div class="text-muted">查看所有注册用户，管理用户权限与状态</div>
        </div>
        <div class="stat-pill">
          <span class="lbl">用户总数</span>
          <span class="val">{{ users.length }}</span>
        </div>
      </div>
    </div>

    <div class="table-card-modern">
      <el-table 
        :data="users" 
        style="width: 100%"
        size="large"
        :header-cell-style="{ background: '#f8fafc', fontWeight: '700', padding: '15px 0' }"
      >
        <el-table-column label="用户 ID" prop="id" width="100" />
        <el-table-column label="用户信息" min-width="200">
          <template #default="scope">
            <div class="d-flex align-items-center">
              <el-avatar :size="40" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <div class="ms-3">
                <div class="fw-bold text-dark">{{ scope.row.username }}</div>
                <div class="text-muted x-small">{{ scope.row.email }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="系统角色" width="150">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ROLE_ADMIN' ? 'danger' : 'info'" effect="light" round>
              {{ scope.row.role === 'ROLE_ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button 
              v-if="scope.row.role !== 'ROLE_ADMIN'"
              type="danger" 
              link 
              icon="Delete" 
              @click="handleDeleteUser(scope.row)"
            >
              注销账号
            </el-button>
            <span v-else class="text-muted small">系统核心</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }
.x-small { font-size: 12px; }

.header-glass {
  background: white;
  padding: 30px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.stat-pill {
  background: #f0f7ff;
  padding: 8px 20px;
  border-radius: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.stat-pill .lbl { color: #606266; font-size: 14px; }
.stat-pill .val { color: #409EFF; font-weight: 800; font-size: 18px; }

.table-card-modern {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0,0,0,0.03);
}

:deep(.el-table__row) {
  transition: background-color 0.3s;
}

:deep(.el-table__row:hover) {
  background-color: #f8fbff !important;
}
</style>
