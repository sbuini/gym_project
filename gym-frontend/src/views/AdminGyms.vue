<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const gyms = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const gymForm = ref({
  id: null,
  name: '',
  location: '',
  price: 0,
  description: '',
  imageUrl: ''
})

const fetchGyms = async () => {
  loading.value = true
  try {
    const res = await axios.get('/api/gyms')
    gyms.value = res.data
  } catch (error) {
    ElMessage.error('获取场馆列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  gymForm.value = { id: null, name: '', location: '', price: 0, description: '', imageUrl: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  gymForm.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    await axios.post('/api/admin/gyms', gymForm.value)
    ElMessage.success(isEdit.value ? '修改成功' : '添加成功')
    dialogVisible.value = false
    fetchGyms()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该场馆吗？此操作不可恢复。', '安全提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    // 模拟删除
    ElMessage.success('场馆已删除（模拟）')
  })
}

onMounted(fetchGyms)
</script>

<template>
  <div class="admin-gyms" v-loading="loading">
    <div class="header-glass mb-5">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <h2 class="fw-bold mb-1">场馆资源管理</h2>
          <div class="text-muted">新增、编辑或删除系统中的场馆信息</div>
        </div>
        <el-button type="primary" icon="Plus" class="add-btn" @click="handleAdd" round>新增场馆</el-button>
      </div>
    </div>

    <div class="table-card-modern">
      <el-table 
        :data="gyms" 
        style="width: 100%"
        size="large"
        :header-cell-style="{ background: '#f8fafc', fontWeight: '700', padding: '15px 0' }"
      >
        <el-table-column label="场馆名称" min-width="150">
          <template #default="scope">
            <div class="d-flex align-items-center">
              <div class="gym-mini-box">
                <img v-if="scope.row.imageUrl" :src="scope.row.imageUrl" />
                <el-icon v-else><Picture /></el-icon>
              </div>
              <span class="ms-3 fw-bold">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="位置" prop="location" min-width="180" />
        <el-table-column label="价格 (元/时)" prop="price" width="120" sortable>
          <template #default="scope">
            <span class="fw-bold text-primary">¥{{ scope.row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" link icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Dialogs -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑场馆' : '新增场馆'" 
      width="550px"
      class="custom-dialog"
      border-radius="20px"
    >
      <el-form :model="gymForm" label-position="top" size="large">
        <el-form-item label="场馆名称" required>
          <el-input v-model="gymForm.name" placeholder="请输入场馆名称" />
        </el-form-item>
        <el-form-item label="位置" required>
          <el-input v-model="gymForm.location" placeholder="请输入场馆位置" />
        </el-form-item>
        <el-form-item label="价格 (元/小时)" required>
          <el-input-number v-model="gymForm.price" :min="1" class="w-100" />
        </el-form-item>
        <el-form-item label="图片链接">
          <el-input v-model="gymForm.imageUrl" placeholder="请输入图片 URL (可选)" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="gymForm.description" type="textarea" :rows="3" placeholder="请输入场馆描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" round>取消</el-button>
          <el-button type="primary" @click="submitForm" round>确定保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.fw-bold { font-weight: 800; }

.header-glass {
  background: white;
  padding: 30px 40px;
  border-radius: 24px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.02);
}

.add-btn {
  padding: 12px 28px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.2);
}

.table-card-modern {
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(0,0,0,0.03);
}

.gym-mini-box {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.gym-mini-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

:deep(.el-dialog) {
  border-radius: 24px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  margin-right: 0;
  padding: 24px 32px;
  border-bottom: 1px solid #f0f2f5;
}

:deep(.el-dialog__body) {
  padding: 32px;
}

:deep(.el-dialog__footer) {
  padding: 16px 32px 32px;
}
</style>
