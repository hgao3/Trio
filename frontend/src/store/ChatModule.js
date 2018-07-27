import * as firebase from 'firebase'
import {AXIOS} from '../components/Dashboard/http-common'

const ChatModule = {
  state: {
    chats: [],
    members: [],
    currentChatId: '',
    currentChatName: ''
  },
  mutations: {
    setMessagesEmpty (state) {
      state.messages = []
    },
    setChats (state, payload) {
      state.chats = payload
    },
    setMembers (state, payload) {
      state.members = [];
      for (const userId in payload.id.members) {
        firebase.database().ref('users').child(userId).on('value', data => {
          let snapshot = data.val();
          let user = {
            id: snapshot.id,
            email: snapshot.email,
            firstname: snapshot.firstname,
            lastname: snapshot.lastname,
            name: snapshot.fullname
          };
          state.members.push(user);
        });
      }
      state.currentChatId = payload.id.id;
      state.currentChatName = payload.id.name;
    }
  },
  actions: {
    sendMessage ({commit}, payload) {
      let chatID = payload.chatID;
      const message = {
        userId: payload.userId,
        fullname: payload.fullname,
        content: payload.content,
        date: firebase.database.ServerValue.TIMESTAMP
      };
      firebase.database().ref('messages').child(chatID).child('messages').push(message)
        .then(
          (data) => {
          }
        )
        .catch(
          (error) => {
            console.log(error)
          }
        );
      for (const user of payload.alert_list) {
        const postData = {
          from: payload.from,
          body: payload.content,
          channel: payload.channel
        };
        const postConfig = {
          headers: {'idToken': payload.idToken}
        };
        AXIOS.post(`/user/${user.email}/notify`, postData, postConfig);
      }
    },
    // loadChats2 ({commit}) {
    //   firebase.database().ref('chats').on('value', function (snapshot) {
    //     commit('setChats', snapshot.val())
    //   })
    // },
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
        name: payload.userId.fullname
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
    addMember ({commit}, payload) {
      firebase.database().ref('users').child(payload.newMember).on('value', function (snapshot) {
        firebase.database().ref('chats').child(payload.roomId).child('members').child(payload.newMember).set({
          name: snapshot.val().name
        })
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
    },
    currentChatName (state) {
      return state.currentChatName;
    }
  }
}

export default ChatModule
