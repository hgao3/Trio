import Vue from 'vue'
import Vuex from 'vuex'
import * as firebase from 'firebase'
import Router from '../router'
import AuthModule from './AuthModule'
import ChatModule from './ChatModule'

Vue.use(Vuex)

export const store = new Vuex.Store({
  modules: {
    auth: AuthModule,
    chat: ChatModule
  },
  state: {
    user: null,
    loading: false,
    error: null,
    onlineUsers: [],
    issueManagerMode: false,
    issueSelectProject: {project_id: 'All', project_title: 'Select All'},
    generalRoomId: '-LGak_O3KJElAXGM3Jgl',
    serverHost: 'http://ec2-54-210-102-133.compute-1.amazonaws.com:8088' //'http://localhost:8088'
  },
  mutations: {
    setLoading (state, payload) {
      state.loading = payload
    },
    setError (state, payload) {
      state.error = payload
    },
    clearError (state) {
      state.error = null
    },
    setOnlineUsers (state, payload) {
      state.onlineUsers = payload
    },
    setIssueManagerMode (state, payload) {
      state.issueManagerMode = payload
    },
    setIssueSelectProject (state, payload) {
      state.issueSelectProject = payload
    }
  },
  actions: {
    loadOnlineUsers ({commit}) {
      firebase.database().ref('presence').on('value', function (snapshot) {
        let result = []
        result[0] = snapshot.numChildren()
        result[1] = snapshot.val()
        commit('setOnlineUsers', result)
      })
    },
    clearError ({commit}) {
      commit('clearError')
    }
  },
  getters: {
    loading (state) {
      return state.loading
    },
    error (state) {
      return state.error
    },
    onlineUsers (state) {
      return state.onlineUsers
    },
    serverHost (state) {
      return state.serverHost
    },
    generalRoomId (state) {
      return state.generalRoomId
    },
    issueManagerMode (state) {
      return state.issueManagerMode
    },
    issueSelectProject (state) {
      return state.issueSelectProject
    }
  }
})
