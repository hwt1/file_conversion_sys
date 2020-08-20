import Vue from 'vue'
import Router from 'vue-router'
import HelloWorld from '@/components/HelloWorld'
import demo from '@/pages/demo'
import upload from '@/pages/upload'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'HelloWorld',
      component: HelloWorld
    },
    {
      path:'/demo',
      name:'demo',
      component:demo
    },{
      path:'/upload',
      name:'upload',
      component:upload
    }
  ]
})
