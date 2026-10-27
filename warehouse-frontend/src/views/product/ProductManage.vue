<template>
  <el-card shadow="never">
    <div class="toolbar">
      <div class="left">
        <el-input
          v-model="query.keyword"
          placeholder="搜索 SKU / 名称 / 分类"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增商品</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe style="width: 100%">
      <el-table-column type="index" label="#" width="55" align="center" />
      <el-table-column prop="sku" label="SKU" min-width="130" />
      <el-table-column prop="name" label="商品名称" min-width="160" />
      <el-table-column prop="category" label="分类" width="110" />
      <el-table-column label="单价" width="110" align="right">
        <template #default="{ row }">¥ {{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column label="库存" width="100" align="center">
        <template #default="{ row }">
          <el-tag :type="row.stock > 0 ? 'success' : 'info'">{{ row.stock ?? 0 }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      class="pager"
      :total="total"
      :page-sizes="[5, 10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="load"
      @size-change="handleSearch"
    />

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="480px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="SKU" prop="sku">
          <el-input v-model="form.sku" placeholder="唯一编码，如 SKU-KB-001" />
        </el-form-item>
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-input v-model="form.category" placeholder="如 外设 / 存储" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="1" style="width: 100%" />
          <div class="hint">库存建议通过「入库管理」累加，这里仅作初始调整</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const loading = ref(false)
const saving = ref(false)
const list = ref([])
const total = ref(0)
const query = reactive({ keyword: '', page: 1, size: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const formRef = ref(null)
const form = reactive({ sku: '', name: '', category: '', price: 0, stock: 0 })

const rules = {
  sku: [{ required: true, message: '请输入 SKU', trigger: 'blur' }],
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const formatPrice = (value) => Number(value ?? 0).toFixed(2)

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/product/list', {
      params: { keyword: query.keyword, page: query.page, size: query.size }
    })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.page = 1
  load()
}

const handleReset = () => {
  query.keyword = ''
  handleSearch()
}

const resetForm = () => {
  form.sku = ''
  form.name = ''
  form.category = ''
  form.price = 0
  form.stock = 0
  editingId.value = null
  formRef.value?.clearValidate()
}

const openCreate = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editingId.value = row.id
  form.sku = row.sku
  form.name = row.name
  form.category = row.category
  form.price = Number(row.price ?? 0)
  form.stock = row.stock ?? 0
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await request.put(`/product/${editingId.value}`, { ...form })
        ElMessage.success('修改成功')
      } else {
        await request.post('/product/save', { ...form })
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      load()
    } catch (e) {
      // 错误提示已由拦截器处理
    } finally {
      saving.value = false
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定删除「${row.name}」吗？该商品的入库记录会一并清理。`,
    '删除确认',
    { type: 'warning', confirmButtonText: '删除', cancelButtonText: '取消' }
  )
    .then(async () => {
      await request.delete(`/product/delete/${row.id}`)
      ElMessage.success('删除成功')
      if (list.value.length === 1 && query.page > 1) query.page -= 1
      load()
    })
    .catch(() => {})
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}
.left {
  display: flex;
  gap: 8px;
}
.pager {
  margin-top: 14px;
  justify-content: flex-end;
}
.hint {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}
</style>
