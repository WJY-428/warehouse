<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="brand">仓库管理系统</div>
      <el-menu
        :default-active="activeMenu"
        router
        class="menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon><span>首页</span>
        </el-menu-item>
        <el-menu-item index="/product">
          <el-icon><Goods /></el-icon><span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/warehouse">
          <el-icon><OfficeBuilding /></el-icon><span>仓库管理</span>
        </el-menu-item>
        <el-menu-item index="/inbound">
          <el-icon><Box /></el-icon><span>入库管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-left">{{ pageTitle }}</div>
        <el-dropdown @command="handleCommand">
          <span class="user">
            {{ nickname }}
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const nickname = computed(() => userStore.userInfo.value?.nickname || userStore.userInfo.value?.username || '管理员')

const titles = {
  '/home': '首页',
  '/product': '商品管理',
  '/warehouse': '仓库管理',
  '/inbound': '入库管理'
}
const pageTitle = computed(() => titles[route.path] || '仓库管理系统')

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
}
.aside {
  background-color: #304156;
}
.brand {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
}
.menu {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}
.header-left {
  font-size: 16px;
  font-weight: 600;
}
.user {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #303133;
}
.main {
  background: #f5f7fa;
}
</style>
