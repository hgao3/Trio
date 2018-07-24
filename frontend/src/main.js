import Vue from 'vue'
import Vuetify from 'vuetify'
import VueResource from 'vue-resource'
import App from './App'
import * as firebase from 'firebase'
import router from './router'
import { store } from './store'

import AlertComponent from './components/Shared/Alert.vue'
import { TableComponent, TableColumn } from 'vue-table-component'


require('../node_modules/vue-table-component/docs/table-component.css')

Vue.use(Vuetify)
Vue.use(VueResource)

// cookies
import VueCookies from 'vue-cookies'
Vue.use(VueCookies)

Vue.config.productionTip = false

Vue.component('app-alert', AlertComponent)
Vue.component('table-component', TableComponent)
Vue.component('table-column', TableColumn)

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
    });


    // Question: not sure how these work... pulled from Firebase documentation

    // As httpOnly cookies are to be used, do not persist any state client side.
    firebase.auth().setPersistence(firebase.auth.Auth.Persistence.NONE);

    // When the user signs in with email and password.
    firebase.auth().signInWithEmailAndPassword('mchen15@bu.edu', 'password').then(user => {
      // Get the user's ID token as it is needed to exchange for a session cookie.
      return user.getIdToken().then(idToken => {
        // Session login endpoint is queried and the session cookie is set.
        // CSRF protection should be taken into account.
        // ...
        const csrfToken = getCookie('csrfToken')
        return postIdTokenToSessionLogin('/sessionLogin', idToken, csrfToken);
      });
    }).then(() => {
      // A page redirect would suffice as the persistence is set to NONE.
      return firebase.auth().signOut();
    }).then(() => {
      window.location.assign('/profile');
    });

  }
})
