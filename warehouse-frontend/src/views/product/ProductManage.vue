<template>
  <div class="product-container">
    <el-card>
      <div class="product-header">
        <el-button type="primary" @click="dialogVisible = true">新增商品</el-button>
      </div>

      <!-- 商品表格 -->
      <el-table :data="productList" border style="width: 100%" :loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="sku" label="SKU" width="120" />
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="scope">￥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100" />
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="danger" icon="Delete" @click="deleteProduct(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 新增商品弹窗 -->
      <el-dialog v-model="dialogVisible" title="新增商品" width="500px">
        <el-form :model="productForm" :rules="rules" ref="productFormRef" label-width="80px">
          <el-form-item label="SKU" prop="sku">
            <el-input v-model="productForm.sku" />
          </el-form-item>
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="productForm.name" />
          </el-form-item>
          <el-form-item label="分类" prop="category">
            <el-input v-model="productForm.category" />
          </el-form-item>
          <el-form-item label="价格" prop="price">
            <el-input v-model.number="productForm.price" type="number" />
          </el-form-item>
          <el-form-item label="库存" prop="stock">
            <el-input v-model.number="productForm.stock" type="number" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveProduct">确定</el-button>
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
const productList = ref([])
const dialogVisible = ref(false)
const productFormRef = ref(null)

// 新增商品表单
const productForm = ref({
  sku: '',
  name: '',
  category: '',
  price: 0,
  stock: 0
})

// 表单校验规则
const rules = ref({
  sku: [{ required: true, message: '请输入SKU', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur', type: 'number' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur', type: 'number' }]
})

// 获取商品列表
const getProductList = async () => {
  loading.value = true
  try {
    const res = await request.get('/product/list')
    productList.value = res.data
  } catch (error) {
    console.error('获取商品列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 新增商品
const saveProduct = async () => {
  try {
    await productFormRef.value.validate()
    await request.post('/product/save', productForm.value)
    ElMessage.success('新增商品成功')
    dialogVisible.value = false
    getProductList()
    // 重置表单
    productForm.value = { sku: '', name: '', category: '', price: 0, stock: 0 }
  } catch (error) {
    console.error('新增商品失败：', error)
  }
}

// 删除商品
const deleteProduct = async (id) => {
  try {
    await request.delete(`/product/delete/${id}`)
    ElMessage.success('删除商品成功')
    getProductList()
  } catch (error) {
    console.error('删除商品失败：', error)
  }
}

// 页面加载时获取商品列表
onMounted(() => {
  getProductList()
})
</script>

<style scoped>
.product-container {
  padding: 20px;
}

.product-header {
  margin-bottom: 20px;
}
</style>