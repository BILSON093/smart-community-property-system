import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/home',
    component: () => import('@/layout/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'news',
        name: 'News',
        component: () => import('@/views/News.vue'),
        meta: { title: '通知' }
      },
      {
        path: 'activity',
        name: 'Activity',
        component: () => import('@/views/Activity.vue'),
        meta: { title: '活动' }
      },
      {
        path: 'activity/:id',
        name: 'ActivityDetail',
        component: () => import('@/views/ActivityDetail.vue'),
        meta: { title: '活动详情' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '我的', needAuth: true }
      },
      {
        path: 'pay',
        name: 'Pay',
        component: () => import('@/views/Pay.vue'),
        meta: { needAuth: true }
      },
      {
        path: 'repair',
        name: 'Repair',
        component: () => import('@/views/Repair.vue'),
        meta: { needAuth: true }
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/Chat.vue'),
        meta: { needAuth: true }
      },
      {
        path: 'forum',
        name: 'Forum',
        component: () => import('@/views/Forum.vue'),
        meta: { needAuth: true }
      },
      {
        path: 'feedback',
        name: 'Feedback',
        component: () => import('@/views/Feedback.vue'),
        meta: { needAuth: true }
      }
    ]
  },
  {
    path: '/news',
    redirect: '/home/news'
  },
  {
    path: '/activity',
    redirect: '/home/activity'
  },
  {
    path: '/forum',
    redirect: '/home/forum'
  },
  {
    path: '/pay',
    redirect: '/home/pay'
  },
  {
    path: '/repair',
    redirect: '/home/repair'
  },
  {
    path: '/chat',
    redirect: '/home/chat'
  },
  {
    path: '/feedback',
    redirect: '/home/feedback'
  },
  {
    path: '/profile',
    redirect: '/home/profile'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/edit-profile',
    name: 'EditProfile',
    component: () => import('@/views/EditProfile.vue'),
    meta: { needAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const whiteList = ['/home', '/news', '/activity', '/home/news', '/home/activity', '/home/forum']
  
  if (to.meta.needAuth && !token && !whiteList.includes(to.path.toLowerCase())) {
    next('/login')
  } else {
    next()
  }
})

export default router
