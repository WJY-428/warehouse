<template>
  <div class="error-container">
    <el-card class="error-card">
      <div class="error-content">
        <h1>500</h1>
        <p>服务器内部错误</p>
        <el-button type="primary" @click="goHome">返回首页</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const goHome = () => {
  router.push('/home')
}
</script>

<style scoped>
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}

.error-card {
  width: 400px;
  text-align: center;
  padding: 20px;
}

.error-content h1 {
  font-size: 80px;
  color: #e6a23c;
  margin-bottom: 20px;
}

.error-content p {
  font-size: 20px;
  margin-bottom: 30px;
}
</style>