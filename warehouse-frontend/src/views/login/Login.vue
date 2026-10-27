<template>
  <div class="login-page">
    <el-card class="login-card">
      <div class="title">仓库管理系统</div>
      <div class="subtitle">Warehouse Management System</div>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" size="large" clearable>
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="submit" :loading="loading" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="tip">默认账号：admin / 123456（首次启动自动创建）</div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/userStore'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login({ ...form })
      ElMessage.success('登录成功')
      router.push('/home')
    } catch (e) {
      // 错误提示已由 axios 拦截器统一处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2d3d 0%, #304156 100%);
}
.login-card {
  width: 380px;
  padding: 10px 20px 20px;
}
.title {
  font-size: 22px;
  font-weight: 700;
  text-align: center;
  margin-top: 10px;
}
.subtitle {
  font-size: 12px;
  color: #909399;
  text-align: center;
  margin: 6px 0 24px;
}
.submit {
  width: 100%;
}
.tip {
  font-size: 12px;
  color: #909399;
  text-align: center;
}
</style>
