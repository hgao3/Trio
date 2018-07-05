import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex);

import {ApiWrapper} from "../components/http-common";

export default new Vuex.Store( {
  state: {
    user: ApiWrapper.getUser(1)
  }
});
