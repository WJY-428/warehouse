<template>
  <div class="warehouse-container">
    <el-card>
      <div class="warehouse-header">
        <el-button type="primary" @click="dialogVisible = true">新增仓库</el-button>
      </div>

      <!-- 仓库表格 -->
      <el-table :data="warehouseList" border style="width: 100%" :loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="仓库编码" width="120" />
        <el-table-column prop="name" label="仓库名称" min-width="200" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="manager" label="管理员" width="120" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增仓库弹窗 -->
      <el-dialog v-model="dialogVisible" title="新增仓库" width="500px">
        <el-form :model="warehouseForm" :rules="rules" ref="warehouseFormRef" label-width="80px">
          <el-form-item label="仓库编码" prop="code">
            <el-input v-model="warehouseForm.code" />
          </el-form-item>
          <el-form-item label="仓库名称" prop="name">
            <el-input v-model="warehouseForm.name" />
          </el-form-item>
          <el-form-item label="地址" prop="address">
            <el-input v-model="warehouseForm.address" />
          </el-form-item>
          <el-form-item label="管理员" prop="manager">
            <el-input v-model="warehouseForm.manager" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveWarehouse">确定</el-button>
        </template>
      </el-dialog>

      <!-- 编辑仓库弹窗 -->
      <el-dialog v-model="editDialogVisible" title="编辑仓库" width="500px">
        <el-form :model="editWarehouseForm" :rules="rules" ref="editWarehouseFormRef" label-width="80px">
          <el-form-item label="仓库编码" prop="code">
            <el-input v-model="editWarehouseForm.code" />
          </el-form-item>
          <el-form-item label="仓库名称" prop="name">
            <el-input v-model="editWarehouseForm.name" />
          </el-form-item>
          <el-form-item label="地址" prop="address">
            <el-input v-model="editWarehouseForm.address" />
          </el-form-item>
          <el-form-item label="管理员" prop="manager">
            <el-input v-model="editWarehouseForm.manager" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="updateWarehouse">确定</el-button>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const warehouseList = ref([])
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const warehouseFormRef = ref(null)
const editWarehouseFormRef = ref(null)

// 新增仓库表单
const warehouseForm = ref({
  code: '',
  name: '',
  address: '',
  manager: ''
})

// 编辑仓库表单
const editWarehouseForm = ref({
  id: '',
  code: '',
  name: '',
  address: '',
  manager: ''
})

// 表单校验规则
const rules = ref({
  code: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  manager: [{ required: true, message: '请输入管理员', trigger: 'blur' }]
})

// 获取仓库列表
const getWarehouseList = async () => {
  loading.value = true
  try {
    const res = await request.get('/warehouse/list')
    warehouseList.value = res.data
  } catch (error) {
    console.error('获取仓库列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 新增仓库
const saveWarehouse = async () => {
  try {
    await warehouseFormRef.value.validate()
    await request.post('/warehouse/save', warehouseForm.value)
    ElMessage.success('新增仓库成功')
    dialogVisible.value = false
    getWarehouseList()
    // 重置表单
    warehouseForm.value = { code: '', name: '', address: '', manager: '' }
  } catch (error) {
    console.error('新增仓库失败：', error)
  }
}

// 处理编辑按钮点击事件
const handleEdit = (row) => {
  // 填充编辑表单数据
  editWarehouseForm.value = { ...row }
  // 打开编辑弹窗
  editDialogVisible.value = true
}

// 更新仓库信息
const updateWarehouse = async () => {
  try {
    await editWarehouseFormRef.value.validate()
    await request.put('/warehouse/update', editWarehouseForm.value)
    ElMessage.success('编辑仓库成功')
    editDialogVisible.value = false
    getWarehouseList()
  } catch (error) {
    console.error('编辑仓库失败：', error)
  }
}

// 页面加载时获取仓库列表
onMounted(() => {
  getWarehouseList()
})
</script>

<style scoped>
.warehouse-container {
  padding: 20px;
}

.warehouse-header {
  margin-bottom: 20px;
}
</style>