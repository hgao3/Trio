import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home'
import Chat from '@/components/Chat/Chat'
import Create from '@/components/Chat/Create'
import Profile from '@/components/User/Profile'
import Signup from '@/components/User/Signup'
import Signin from '@/components/User/Signin'
import Signout from '@/components/User/Signout'
import UsersTable from '@/components/User/UsersTable'
import Dashboard from '@/components/Dashboard/Dashboard'
import IssueTracker from '@/components/IssueTracker/IssueTracker'
import Issue from '@/components/IssueTracker/Issue'
import CreateIssue from '@/components/IssueTracker/CreateIssue'
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
      path: '/logout',
      name: 'Signout',
      component: Signout
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
/*    {
      path: '/UsersTable',
      name: 'UsersTable',
      component: UsersTable,
      beforeEnter: AuthGuard
    },*/
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard,
      beforeEnter: AuthGuard
    },
    {
      path: '/dashboard/:project_id',
      name: 'Dashboard',
      component: Dashboard,
      beforeEnter: AuthGuard
    },
    {
      path: '/issueTracker',
      name: 'IssueTracker',
      component: IssueTracker,
      beforeEnter: AuthGuard
    },
    {
      path: '/issue/:id',
      name: 'Issue',
      component: Issue,
      props: true,
      beforeEnter: AuthGuard
    },
    {
      path: '/createIssue',
      name: 'CreateIssue',
      component: CreateIssue,
      props: true,
      beforeEnter: AuthGuard
    }
  ],
  mode: 'history'
})
