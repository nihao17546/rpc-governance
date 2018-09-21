import Vue from 'vue'
import Router from 'vue-router'
import VueResource from 'vue-resource'
import index from '../components/index'
import login from '../components/login'

Vue.use(Router)
Vue.use(VueResource)

export default new Router({
  // mode: 'history',
  routes: [
    {
      path: '/',
      name: 'index',
      component: index
    },
    {
      path: '/login',
      name: 'login',
      component: login
    }
  ]
})
