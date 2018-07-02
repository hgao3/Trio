import * as firebase from 'firebase'

const ChatModule = {
  state: {
    chats: []
  },
  mutations: {
    setMessagesEmpty (state) {
      state.messages = []
    },
    setChats (state, payload) {
      state.chats = payload
    }
  },
  actions: {
    sendMessage ({commit}, payload) {
      let chatID = payload.chatID
      const message = {
        userId: payload.userId,
        username: payload.username,
        content: payload.content,
        date: firebase.database.ServerValue.TIMESTAMP
      }
      firebase.database().ref('messages').child(chatID).child('messages').push(message)
        .then(
          (data) => {
          }
        )
        .catch(
          (error) => {
            console.log(error)
          }
        )
    },
    loadChats2 ({commit}) {
      firebase.database().ref('chats').on('value', function (snapshot) {
        commit('setChats', snapshot.val())
      })
    },
    loadChats ({commit}, payload) {
      // filter chat room by authorizedUsers
      firebase.database().ref('chats').once('value', function(snapshot) {
        var rooms = snapshot.val()
        var list = []
        for (var roomId in rooms) {
          for (var user in rooms[roomId].authorizedUsers) {
            if (user === payload.userId) {
              list.push(rooms[roomId])
            }
          }
        }
        commit('setChats', list)
      })
    },
    createChat ({commit}, payload) {
      var newPostKey = firebase.database().ref().child('room-metadata').push().key
      var updates = {}
      var list = {}
      list[payload.userId] = true
      updates['/chats/' + newPostKey] = {
        id: newPostKey,
        name: payload.chatName,
        createdByUserId: payload.userId,
        createdAt: firebase.database.ServerValue.TIMESTAMP,
        authorizedUsers: list
      }

      firebase.database().ref().update(updates)
      firebase.database().ref('users').child(payload.userId).child('rooms').child(newPostKey).set({
        id: newPostKey,
        name: payload.chatName,
        active: true
      })

      return new Promise((resolve, reject) => {
        resolve(newPostKey)
      })
    },
    createCha2 ({commit}, payload) {
      var newPostKey = firebase.database().ref().child('chats').push().key
      var updates = {}
      updates['/chats/' + newPostKey] = {name: payload.chatName}
      firebase.database().ref().update(updates)
      return new Promise((resolve, reject) => {
        resolve(newPostKey)
      })
    }
  },
  getters: {
    messages (state) {
      return state.messages
    },
    chats (state) {
      return state.chats
    }
  }
}

export default ChatModule
