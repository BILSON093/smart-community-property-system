import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/login',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/Admin.vue'),
        meta: { title: '管理员管理' }
      },
      {
        path: 'owner',
        name: 'Owner',
        component: () => import('@/views/Owner.vue'),
        meta: { title: '业主管理' }
      },
      {
        path: 'worker',
        name: 'Worker',
        component: () => import('@/views/Worker.vue'),
        meta: { title: '维修员管理' }
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/Repair.vue'),
        meta: { title: '报修管理' }
      },
      {
        path: 'fee',
        name: 'Fee',
        component: () => import('@/views/Fee.vue'),
        meta: { title: '缴费管理' }
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('@/views/Notice.vue'),
        meta: { title: '通知公告' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/Activity.vue'),
        meta: { title: '社区活动' }
      },
      {
        path: 'carousel',
        name: 'Carousel',
        component: () => import('@/views/Carousel.vue'),
        meta: { title: '轮播图管理' }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/Forum.vue'),
        meta: { title: '论坛管理' }
      },
      {
        path: 'forum-category',
        name: 'ForumCategory',
        component: () => import('@/views/ForumCategory.vue'),
        meta: { title: '论坛分类管理' }
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/Chat.vue'),
        meta: { title: '在线客服' }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/Feedback.vue'),
        meta: { title: '留言反馈' }
      },
      {
        path: 'ai-config',
        name: 'AiConfig',
        component: () => import('@/views/AiConfig.vue'),
        meta: { title: 'AI模型配置' }
      }
    ]
  },
  {
    path: '/worker-home',
    component: () => import('@/worker-layout/WorkerLayout.vue'),
    children: [
      {
        path: '',
        name: 'WorkerHome',
        component: () => import('@/views/WorkerHome.vue'),
        meta: { title: '工单大厅' }
      },
      {
        path: 'completed',
        name: 'WorkerCompleted',
        component: () => import('@/views/WorkerCompleted.vue'),
        meta: { title: '已完成订单' }
      },
      {
        path: 'profile',
        name: 'WorkerProfile',
        component: () => import('@/views/WorkerProfile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    if (userInfo.role === 0) {
      next('/dashboard')
    } else if (userInfo.role === 2) {
      next('/worker-home')
    }
  } else {
    next()
  }
})

export default router

