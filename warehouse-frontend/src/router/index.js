import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/userStore'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/home',
    children: [
      { path: 'home', name: 'Home', component: () => import('@/views/home/Home.vue') },
      { path: 'product', name: 'Product', component: () => import('@/views/product/ProductManage.vue') },
      { path: 'warehouse', name: 'Warehouse', component: () => import('@/views/warehouse/WarehouseManage.vue') },
      { path: 'inbound', name: 'Inbound', component: () => import('@/views/inbound/InboundManage.vue') }
    ]
  },
  { path: '/404', name: 'NotFound', component: () => import('@/views/error/Error404.vue') },
  { path: '/500', name: 'ServerError', component: () => import('@/views/error/Error500.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/404' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 只有「有没有 token」这一道前端校验；真正的权限由后端拦截器把关
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  userStore.initUser()
  const logged = userStore.isLogin()

  if (to.path !== '/login' && !logged) {
    next('/login')
    return
  }
  if (to.path === '/login' && logged) {
    next('/home')
    return
  }
  next()
})

export default router
