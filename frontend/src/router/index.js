import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home'
import Chat from '@/components/Chat/Chat'
import Create from '@/components/Chat/Create'
import Profile from '@/components/User/Profile'
import Signup from '@/components/User/Signup'
import Signin from '@/components/User/Signin'
import UsersTable from '@/components/User/UsersTable'
import Dashboard from '@/components/Dashboard/Dashboard'
import IssueTracker from '@/components/IssueTracker/IssueTracker'
import AuthGuard from './auth-guard'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home,
      beforeEnter: AuthGuard
    },
    {
      path: '/login',
      name: 'Signin',
      component: Signin
    },
    {
      path: '/register',
      name: 'Signup',
      component: Signup
    },
    {
      path: '/profile',
      name: 'Profile',
      component: Profile
    },
    {
      path: '/chat/:id',
      name: 'Chat',
      component: Chat,
      props: true,
      beforeEnter: AuthGuard
    },
    {
      path: '/create',
      name: 'CreateChat',
      component: Create,
      beforeEnter: AuthGuard
    },
    {
      path: '/UsersTable',
      name: 'UsersTable',
      component: UsersTable,
      beforeEnter: AuthGuard
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard,
      beforeEnter: AuthGuard
    },
    {
      path: '/IssueTracker',
      name: 'IssueTracker',
      component: IssueTracker,
      beforeEnter: AuthGuard
    }
  ],
  mode: 'history'
})
