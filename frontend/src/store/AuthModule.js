import * as firebase from 'firebase'

const AuthModule = {
  state: {
    user: null
  },
  mutations: {
    setUser (state, payload) {
      state.user = payload
      const userListRef = firebase.database().ref('presence')
      const myUserRef = userListRef.push()

      firebase.database().ref('.info/connected')
        .on(
          'value', function (snap) {
            if (snap.val()) {
              // if we lose network then remove this user from the list
              myUserRef.onDisconnect().remove()
              // set user's online status
              let presenceObject = {user: payload.username, status: 'online'}
              myUserRef.set(presenceObject)
            } else {
              // client has lost network
              let presenceObject = {user: payload.username, status: 'offline'}
              myUserRef.set(presenceObject)
            }
          }
        )
    },
    setUserOnly (state, payload) {
      state.user = payload
    }
  },
  actions: {
    signUserUp ({commit}, payload) {
      commit('setLoading', true)
      commit('clearError')
      firebase.auth().createUserWithEmailAndPassword(payload.email, payload.password)
        .then(
          user => {
            firebase.database().ref('users').child(user.uid).set({
              name: payload.username,
              firstname: payload.firstname,
              email: payload.email,
              lastname: payload.lastname,
              id: user.uid,
              rooms: []
            })
              .then(
                message => {
                  commit('setLoading', false)
                  const newUser = {
                    id: user.uid,
                    username: payload.username
                  }
                  commit('setUser', newUser)
                }
              )
              .catch(
                error => {
                  commit('setLoading', false)
                  commit('setError', error)
                }
              )
          }
        )
        .catch(
          error => {
            commit('setLoading', false)
            commit('setError', error)
          }
        )
    },
    signUserIn ({commit}, payload) {
      commit('setLoading', true)
      commit('clearError')
      firebase.auth().signInWithEmailAndPassword(payload.email, payload.password)
        .then(
          user => {
            firebase.database().ref('users').child(user.uid).once('value', function (data) {
              commit('setLoading', false)
              const newUser = {
                id: user.uid,
                username: data.val().name,
                firstname: data.val().firstname,
                lastname: data.val().lastname,
                email: data.val().email,
                rooms: data.val().rooms
              }
              commit('setUser', newUser)
            })
          }
        )
        .catch(
          error => {
            commit('setLoading', false)
            commit('setError', error)
          }
        )
    },
    getIdToken ({commit}, user) {
      commit('setLoading', true)
      commit('clearError')
      firebase.auth().currentUser.getIdToken(/* forceRefresh */ true)
        .then(function (idToken) {
          const newUser = {
            id: user.id,
            username: user.username,
            idToken: idToken,
            rooms: user.rooms
          }
          commit('setUserOnly', newUser)
        }
        )
        .catch(
          error => {
            commit('setLoading', false)
            commit('setError', error)
          }
        )
    }
  },
  getters: {
    user (state) {
      return state.user
    }
  }
}

export default AuthModule
