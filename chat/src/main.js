import Vue from 'vue'
import Vuetify from 'vuetify'
import VueResource from 'vue-resource'
import App from './App'
import * as firebase from 'firebase'
import router from './router'
import { store } from './store'

import AlertComponent from './components/Shared/Alert.vue'

Vue.use(Vuetify)
Vue.use(VueResource)

Vue.config.productionTip = false

Vue.component('app-alert', AlertComponent)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App),
  created () {
    firebase.initializeApp({
      apiKey: 'AIzaSyCW9gXR5G6Gr1-77Cy07oUKc4qjG-AJYpo',
      authDomain: 'trio-cs673-firebase.firebaseapp.com',
      databaseURL: 'https://trio-cs673-firebase.firebaseio.com',
      projectId: 'trio-cs673-firebase',
      storageBucket: 'trio-cs673-firebase.appspot.com'
    })
  }
})
