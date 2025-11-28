import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/userStore'
import Layout from '@/components/Layout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/login/Login.vue')
    },
    {
      path: '/',
      component: Layout,
      redirect: '/home',
      children: [
        { path: 'home', name: 'home', component: () => import('@/views/home/Home.vue') },
        { path: 'product', name: 'product', component: () => import('@/views/product/ProductManage.vue') },
        { path: 'warehouse', name: 'warehouse', component: () => import('@/views/warehouse/WarehouseManage.vue') },
        { path: 'inbound', name: 'inbound', component: () => import('@/views/inbound/InboundManage.vue') }
      ]
    },
    { path: '/404', component: () => import('@/views/error/Error404.vue') },
    { path: '/500', component: () => import('@/views/error/Error500.vue') },
    { path: '/:pathMatch(.*)*', redirect: '/404' }
  ]
})

// 路由守卫：未登录跳转到登录页
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  userStore.initUser()
  if (to.name !== 'login' && !userStore.userInfo) {
    next({ name: 'login' })
  } else {
    next()
  }
})

export default router