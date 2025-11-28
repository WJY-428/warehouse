<template>
  <div class="inbound-container">
    <el-card>
      <div class="inbound-header">
        <el-button type="primary" @click="dialogVisible = true">新增入库记录</el-button>
      </div>

      <!-- 入库记录表格 -->
      <el-table :data="inboundList" border style="width: 100%" :loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="商品名称" min-width="200">
          <template #default="scope">{{ scope.row.product?.name }}</template>
        </el-table-column>
        <el-table-column label="仓库名称" min-width="200">
          <template #default="scope">{{ scope.row.warehouse?.name }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="入库数量" width="120" />
        <el-table-column prop="inboundTime" label="入库时间" width="200" />
      </el-table>

      <!-- 新增入库记录弹窗 -->
      <el-dialog v-model="dialogVisible" title="新增入库记录" width="500px">
        <el-form :model="inboundForm" :rules="rules" ref="inboundFormRef" label-width="80px">
          <el-form-item label="商品" prop="productId">
            <el-select v-model="inboundForm.productId" placeholder="请选择商品">
              <el-option
                v-for="item in productList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="仓库" prop="warehouseId">
            <el-select v-model="inboundForm.warehouseId" placeholder="请选择仓库">
              <el-option
                v-for="item in warehouseList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="入库数量" prop="quantity">
            <el-input v-model.number="inboundForm.quantity" type="number" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveInbound">确定</el-button>
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
const inboundList = ref([])
const productList = ref([])
const warehouseList = ref([])
const dialogVisible = ref(false)
const inboundFormRef = ref(null)

// 新增入库记录表单
const inboundForm = ref({
  productId: '',
  warehouseId: '',
  quantity: 0
})

// 表单校验规则
const rules = ref({
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur', type: 'number', min: 1 }]
})

// 获取入库记录列表
const getInboundList = async () => {
  loading.value = true
  try {
    const res = await request.get('/inbound/list')
    inboundList.value = res.data
  } catch (error) {
    console.error('获取入库记录失败：', error)
  } finally {
    loading.value = false
  }
}

// 获取商品和仓库列表（用于下拉选择）
const getOptions = async () => {
  try {
    const [productRes, warehouseRes] = await Promise.all([
      request.get('/product/list'),
      request.get('/warehouse/list')
    ])
    productList.value = productRes.data
    warehouseList.value = warehouseRes.data
  } catch (error) {
    console.error('获取选项失败：', error)
  }
}

// 新增入库记录
const saveInbound = async () => {
  try {
    await inboundFormRef.value.validate()
    // 构造请求参数（关联商品和仓库）
    const params = {
      product: { id: inboundForm.value.productId },
      warehouse: { id: inboundForm.value.warehouseId },
      quantity: inboundForm.value.quantity
    }
    await request.post('/inbound/save', params)
    ElMessage.success('新增入库记录成功')
    dialogVisible.value = false
    getInboundList()
    // 重置表单
    inboundForm.value = { productId: '', warehouseId: '', quantity: 0 }
  } catch (error) {
    console.error('新增入库记录失败：', error)
  }
}

// 页面加载时初始化数据
onMounted(async () => {
  await getOptions()
  getInboundList()
})
</script>

<style scoped>
.inbound-container {
  padding: 20px;
}

.inbound-header {
  margin-bottom: 20px;
}
</style>