import * as firebase from 'firebase'

const ChatModule = {
  state: {
    chats: [],
    members: [],
    currentChatId: ''
  },
  mutations: {
    setMessagesEmpty (state) {
      state.messages = []
    },
    setChats (state, payload) {
      state.chats = payload
    },
    setMembers (state, payload) {
      var list = []
      for (var userId in payload.id.members) {
        list.push({name: payload.id.members[userId].name, id: userId})
      }
      state.members = list
      state.currentChatId = payload.id.id
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
      // filter chat room by members
      firebase.database().ref('chats').once('value', function (snapshot) {
        var rooms = snapshot.val()
        var list = []
        for (var roomId in rooms) {
          for (var user in rooms[roomId].members) {
            if (user === payload.userId) {
              list.push(rooms[roomId])
            }
          }
        }
        commit('setChats', list)
      })
    },
    loadMembers ({commit}, payload) {
      // filter chat room by members
      firebase.database().ref('chats').child(payload.roomId).child('members').once('value', function (snapshot) {
        var rooms = snapshot.val()
        var list = []
        for (var roomId in rooms) {
          for (var user in rooms[roomId].members) {
            if (user === payload.userId) {
              list.push(rooms[roomId])
            }
          }
        }
        commit('setMembers', list)
      })
    },
    createChat ({commit}, payload) {
      var newPostKey = firebase.database().ref().child('chats').push().key
      var updates = {}
      updates['/chats/' + newPostKey] = {
        id: newPostKey,
        name: payload.chatName,
        createdByUserId: payload.userId.id,
        createdAt: firebase.database.ServerValue.TIMESTAMP,
        members: []
      }
      firebase.database().ref().update(updates)
      firebase.database().ref('chats').child(newPostKey).child('members').child(payload.userId.id).set({
        name: payload.userId.username
      })
      firebase.database().ref('users').child(payload.userId.id).child('rooms').child(newPostKey).set({
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
    },
    addMember ({commit}, payload) {
      firebase.auth().getUserByEmail(payload.newMember)
        .then(function (userRecord) {
          // See the UserRecord reference doc for the contents of userRecord.
          console.log('Successfully fetched user data:', userRecord.toJSON())
          firebase.database().ref('chats').child(payload.roomId).child('members').child(userRecord.id).set({
            name: payload.userId.username
          })
        })
        .catch(function(error) {
          console.log('Error fetching user data:', error)
        })
    }
  },
  getters: {
    messages (state) {
      return state.messages
    },
    chats (state) {
      return state.chats
    },
    members (state) {
      return state.members
    },
    currentChatId (state) {
      return state.currentChatId
    }
  }
}

export default ChatModule
