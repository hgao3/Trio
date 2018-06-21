import Vuex from 'vuex'
import Vue from 'vue'

Vue.use(Vuex);

export default new Vuex.Store( {
  state: {
    user: {
      "user_id": 1,
      "email": "hgao3@bu.edu",
      "first_name": "Hugh",
      "last_name": "Gao",
      "is_admin": true,
      "projects": [
        "2",
        "1",
        "3",
        "4"
      ],
      "projects_managing": [
        "2",
        "1",
        "3",
        "4"
      ]
    }
  }
});
