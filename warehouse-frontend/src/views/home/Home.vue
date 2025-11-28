<template>
  <div class="home-container">
    <el-card>
      <div class="welcome">
        <h1>欢迎使用仓库管理系统</h1>
        <p>当前用户：{{ userStore.userInfo?.nickname }}</p>
        <p>今日日期：{{ today }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/userStore'
import { ref } from 'vue'

const userStore = useUserStore()
const today = ref(new Date().toLocaleDateString())
</script>

<style scoped>
.home-container {
  padding: 20px;
}

.welcome {
  text-align: center;
  padding: 50px 0;
}

.welcome h1 {
  color: #1989fa;
  margin-bottom: 20px;
}
</style>