import {store} from '../store/index'

// cookies
import Vue from 'vue'
import VueCookies from 'vue-cookies'
Vue.use(VueCookies)

export default (to, from, next) => {
  if (store.getters.user) {
    next()
  } else {
    next('/login')
  }

  // Question: do I put this here???

  this.$cookies.set(store.getters.user, true, 'default_unit_second', 'input_value', 60+30) // 1 minute 30 second after, expire
//  this.$cookies.set(store.getters.user, true, 'default_unit_second', 'input_value', 0) // end of browser session, expire

}
