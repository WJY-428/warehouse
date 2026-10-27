<template>
  <el-card shadow="never">
    <div class="toolbar">
      <div class="left">
        <el-input
          v-model="query.keyword"
          placeholder="搜索编码 / 名称 / 地址 / 管理员"
          clearable
          style="width: 260px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增仓库</el-button>
    </div>

    <el-table v-loading="loading" :data="list" border stripe style="width: 100%">
      <el-table-column type="index" label="#" width="55" align="center" />
      <el-table-column prop="code" label="仓库编码" min-width="130" />
      <el-table-column prop="name" label="仓库名称" min-width="150" />
      <el-table-column prop="address" label="地址" min-width="200" />
      <el-table-column prop="manager" label="管理员" width="120" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑仓库' : '新增仓库'" width="480px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="仓库编码" prop="code">
          <el-input v-model="form.code" placeholder="唯一编码，如 WH-LS-01" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入仓库名称" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="管理员" prop="manager">
          <el-input v-model="form.manager" placeholder="请输入管理员姓名" />
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
const form = reactive({ code: '', name: '', address: '', manager: '' })

const rules = {
  code: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }]
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/warehouse/list', {
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
  form.code = ''
  form.name = ''
  form.address = ''
  form.manager = ''
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
  form.code = row.code
  form.name = row.name
  form.address = row.address
  form.manager = row.manager
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      if (isEdit.value) {
        await request.put(`/warehouse/${editingId.value}`, { ...form })
        ElMessage.success('修改成功')
      } else {
        await request.post('/warehouse/save', { ...form })
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
  ElMessageBox.confirm(`确定删除仓库「${row.name}」吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '删除',
    cancelButtonText: '取消'
  })
    .then(async () => {
      await request.delete(`/warehouse/delete/${row.id}`)
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
</style>
