<template>
  <el-container style="height: 100vh;">
    <el-aside width="200px" style="background-color: #2e3b4e;">
      <el-menu
        default-active="1"
        class="el-menu-vertical-demo"
        background-color="#2e3b4e"
        text-color="#fff"
        active-text-color="#ffd04b"
        router
      >
        <el-menu-item index="/home">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-menu-item index="/product">
          <el-icon><Goods /></el-icon>
          <template #title>商品管理</template>
        </el-menu-item>
        <el-menu-item index="/warehouse">
          <el-icon><Box /></el-icon>
          <template #title>仓库管理</template>
        </el-menu-item>
        <el-menu-item index="/inbound">
          <el-icon><Upload /></el-icon>
          <template #title>入库管理</template>
        </el-menu-item>
        <el-menu-item @click="logout">
          <el-icon><SwitchButton /></el-icon>
          <template #title>退出登录</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="text-align: right; font-size: 12px">
        <el-dropdown>
          <i class="el-icon-setting" style="margin-right: 15px"></i>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>查看</el-dropdown-item>
              <el-dropdown-item>新增</el-dropdown-item>
              <el-dropdown-item>删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <span>{{ userStore.userInfo?.nickname }}</span>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useUserStore } from '@/store/userStore'
import { useRouter } from 'vue-router'
import { House, Goods, Box, Upload, SwitchButton } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()

const logout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>