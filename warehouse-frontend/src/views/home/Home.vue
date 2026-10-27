<template>
  <div class="home-container">
    <el-card>
      <div class="welcome">
        <h1>欢迎使用仓库管理系统</h1>
        <p>当前用户：{{ nickname }}</p>
        <p>今日日期：{{ today }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store/userStore'
import { computed, ref } from 'vue'

// userStore 是普通对象包着 ref，脚本里要用 .value 取值
const { userInfo } = useUserStore()
const nickname = computed(() => userInfo.value?.nickname || userInfo.value?.username || '管理员')
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