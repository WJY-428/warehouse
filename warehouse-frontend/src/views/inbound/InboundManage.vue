<template>
  <el-card shadow="never">
    <div class="toolbar">
      <div class="left">
        <el-input
          v-model="query.keyword"
          placeholder="搜索商品名称 / 仓库名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增入库</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe style="width: 100%">
      <el-table-column type="index" label="#" width="55" align="center" />
      <el-table-column label="商品" min-width="170">
        <template #default="{ row }">{{ row.product?.name || '-' }}</template>
      </el-table-column>
      <el-table-column label="仓库" min-width="150">
        <template #default="{ row }">{{ row.warehouse?.name || '-' }}</template>
      </el-table-column>
      <el-table-column label="入库数量" width="110" align="center">
        <template #default="{ row }">
          <el-tag type="success">+{{ row.quantity }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inboundTime" label="入库时间" width="180" />
      <el-table-column label="操作" width="120" align="center">
        <template #default="{ row }">
          <el-button link type="danger" @click="handleCancel(row)">撤销</el-button>
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

    <el-dialog v-model="dialogVisible" title="新增入库" width="480px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="商品" prop="productId">
          <el-select v-model="form.productId" placeholder="请选择商品" filterable style="width: 100%">
            <el-option
              v-for="item in products"
              :key="item.id"
              :label="`${item.name}（当前库存 ${item.stock ?? 0}）`"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="仓库" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择仓库" filterable style="width: 100%">
            <el-option v-for="item in warehouses" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="入库数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" :step="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSubmit">确认入库</el-button>
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
const products = ref([])
const warehouses = ref([])
const query = reactive({ keyword: '', page: 1, size: 10 })

const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({ productId: null, warehouseId: null, quantity: 1 })

const rules = {
  productId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  warehouseId: [{ required: true, message: '请选择仓库', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入入库数量', trigger: 'blur' }]
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/inbound/list', {
      params: { keyword: query.keyword, page: query.page, size: query.size }
    })
    list.value = res.data.list
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadOptions = async () => {
  const [p, w] = await Promise.all([request.get('/product/options'), request.get('/warehouse/options')])
  products.value = p.data
  warehouses.value = w.data
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
  form.productId = null
  form.warehouseId = null
  form.quantity = 1
  formRef.value?.clearValidate()
}

const openCreate = async () => {
  resetForm()
  await loadOptions()
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      await request.post('/inbound/save', { ...form })
      ElMessage.success('入库成功，库存已累加')
      dialogVisible.value = false
      handleSearch()
    } catch (e) {
      // 错误提示已由拦截器处理
    } finally {
      saving.value = false
    }
  })
}

const handleCancel = (row) => {
  ElMessageBox.confirm(
    `撤销后将从「${row.product?.name}」的库存中扣回 ${row.quantity}，确定撤销吗？`,
    '撤销入库',
    { type: 'warning', confirmButtonText: '确定撤销', cancelButtonText: '取消' }
  )
    .then(async () => {
      await request.delete(`/inbound/${row.id}`)
      ElMessage.success('已撤销')
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
</style>
