<template>
  <v-layout row>
    <v-flex xs12 sm10 order-xs2 style="position: relative;">
      <div class="chat-container">
        <message :messages="messages" @imageLoad="scrollToEnd"></message>
      </div>
      <alert-picker :show="alertPanel" @close="toggleAlertPanel" :alert_list="alert_list"></alert-picker>
      <emoji-picker :show="emojiPanel" @close="toggleEmojiPanel" @click="addMessage"></emoji-picker>
      <div class="typer">
        <input type="text" placeholder="Type here..." v-on:keyup.enter="sendMessage" v-model="content">
        <v-btn icon class="blue--text icon" @click="toggleAlertPanel">
          <v-icon>add_alert</v-icon>
        </v-btn>
        <v-btn icon class="blue--text icon" @click="toggleEmojiPanel">
          <v-icon>mood</v-icon>
        </v-btn>
      </div>
      <div v-if="alert_list.length > 0">
        Sending this message will alert
        <span class="alert_names">{{ alert_list.map(user=>{return `${user.firstname} ${user.lastname}`}).join(', ') }}</span>
      </div>
    </v-flex>
    <v-flex sm2 order-xs1 class="scrollable">
      <chats></chats>
      <members></members>
    </v-flex>
  </v-layout>
</template>

<script>
  import Message from './Message.vue'
  import AlertPicker from './AlertPicker'
  import EmojiPicker from './EmojiPicker.vue'
  import Chats from './Chats.vue'
  import Members from './RoomMembers.vue'
  import * as firebase from 'firebase'
  import Alert from "../Shared/Alert";

  export default {
    data () {
      return {
        content: '',
        chatMessages: [],
        emojiPanel: false,
        alertPanel: false,
        currentRef: {},
        alert_list: []
      }
    },
    props: [
      'id'
    ],
    mounted () {
      this.loadChat();
      this.$store.dispatch('loadOnlineUsers');
    },
    created () {
      this.$store.dispatch('loadChats', { userId: this.$store.getters.user.id })
    },
    components: {
      Alert,
      'message': Message,
      'emoji-picker': EmojiPicker,
      'chats': Chats,
      'members': Members,
      'alert-picker': AlertPicker
    },
    computed: {
      messages () {
        return this.chatMessages
      },
      username () {
        return this.$store.getters.user.username
      },
      userId () {
        return this.$store.getters.user.id
      },
      onChildAdded () {
        var that = this
        var onChildAdded = function (snapshot) {
          let message = snapshot.val()
          /*eslint-disable */
          var urlPattern = /(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig
          /*eslint-enable */
          message.content = message.content
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#039;')
          message.content = message.content.replace(urlPattern, "<a href='$1'>$1</a>")
          that.chatMessages.push(that.processMessage(message))
        }
        return onChildAdded
      }
    },
    watch: {
      'chatMessages': function (value) {
        this.$nextTick(() => {
          this.scrollToEnd()
        })
      },
      '$route.params.id' (newId, oldId) {
        this.currentRef.off('child_added', this.onChildAdded)
        this.loadChat()
      },
      '$store.getters.chats' (newId, oldId) {
        if (newId !== undefined) {
          this.$store.commit('setMembers', {id: this.$store.getters.chats[this.id]})
        }
      }
    },
    methods: {
      loadChat () {
        if (this.id !== undefined) {
          this.chatMessages = []
          let chatID = this.id
          this.currentRef = firebase.database().ref('messages').child(chatID).child('messages')/*.limitToLast(20)*/
          this.currentRef.on('child_added', this.onChildAdded)
          if (this.$store.getters.chats !== undefined && this.$store.getters.chats.length > 0) {
            this.$store.commit('setMembers', {id: this.$store.getters.chats[this.id]})
          }
        }
      },
      processMessage (message) {
        /*eslint-disable */
        var imageRegex = /([^\s\']+).(?:jpg|jpeg|gif|png)/i
        /*eslint-enable */
        if (imageRegex.test(message.content)) {
          message.image = imageRegex.exec(message.content)[0]
        }
        var emojiRegex = /([\u{1f300}-\u{1f5ff}\u{1f900}-\u{1f9ff}\u{1f600}-\u{1f64f}\u{1f680}-\u{1f6ff}\u{2600}-\u{26ff}\u{2700}-\u{27bf}\u{1f1e6}-\u{1f1ff}\u{1f191}-\u{1f251}\u{2934}-\u{1f18e}])/gu
        if (emojiRegex.test(message.content)) {
          message.content = message.content.replace(emojiRegex, '<span class="emoji">$1</span>')
        }
        return message
      },
      sendMessage () {
        if (this.content !== '') {
          const payload = {
            userId: this.userId,
            username: this.username,
            content: this.content,
            chatID: this.id,
            from: this.$store.getters.user.email,
            idToken: this.$store.getters.user.idToken,
            channel: this.$store.getters.currentChatName,
            alert_list: this.alert_list };
          this.$store.dispatch('sendMessage', payload);
          this.content = '';
          this.alert_list = [];
          this.alertPanel = false;
          this.emojiPanel = false;
        }
      },
      scrollToEnd () {
        var container = this.$el.querySelector('.chat-container')
        container.scrollTop = container.scrollHeight
      },
      addMessage (emoji) {
        this.content += emoji.value
      },
      toggleEmojiPanel () {
        this.emojiPanel = !this.emojiPanel;
        if (this.emojiPanel) { this.alertPanel = false; }
      },
      toggleAlertPanel() {
        this.alertPanel = !this.alertPanel;
        if (this.alertPanel) { this.emojiPanel = false; }
      }
    }
  }
</script>

<style>
  .scrollable {
    overflow-y: auto;
    height: 90vh;
  }
  .typer{
    box-sizing: border-box;
    display: flex;
    align-items: center;
    bottom: 0;
    height: 4.9rem;
    width: 100%;
    background-color: #fff;
    box-shadow: 0 -5px 10px -5px rgba(0,0,0,.2);
  }
  .typer .icon {
    margin: 0;
  }
  .typer input[type=text]{
    position: absolute;
    left: 5rem;
    padding: 1rem;
    width: 80%;
    background-color: transparent;
    border: none;
    outline: none;
    font-size: 1.25rem;
  }
  .chat-container{
    box-sizing: border-box;
    height: calc(100vh - 9.5rem);
    overflow-y: auto;
    padding: 10px;
    background-color: #f2f2f2;
  }
  .message{
    margin-bottom: 3px;
  }
  .message.own{
    text-align: right;
  }
  .message.own .content{
    background-color: lightskyblue;
  }
  .chat-container .username{
    font-size: 18px;
    font-weight: bold;
  }
  .chat-container .content{
    padding: 8px;
    background-color: lightgreen;
    border-radius: 10px;
    display:inline-block;
    box-shadow: 0 1px 3px 0 rgba(0,0,0,0.2), 0 1px 1px 0 rgba(0,0,0,0.14), 0 2px 1px -1px rgba(0,0,0,0.12);
    max-width: 50%;
    word-wrap: break-word;
    }
  @media (max-width: 480px) {
    .chat-container .content{
      max-width: 60%;
    }
  }

  .alert_names {
    font-weight: bold;
  }

</style>
