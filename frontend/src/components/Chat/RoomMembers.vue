<template>
  <div>
  <v-list subheader class="line">
    <v-subheader>Members</v-subheader>
    <v-list-tile avatar v-for="(member, index) in members" v-bind:key="member.name">
      <v-list-tile-content>
        <v-list-tile-title v-html="member.name"></v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
    <v-layout subheader class="new-member">
      <v-flex xs12>
        <v-btn v-if="!adding_new_member" @click="adding_new_member = true">Add User</v-btn>
        <UserPicker v-if="adding_new_member" @pick-user="addMember" @cancel-pick="adding_new_member = false"></UserPicker>
      </v-flex>
    </v-layout>
  </v-list>
  <button class="delete_button" v-if="ownerOfChat" @click="deletingChat = true" color="warning">Delete Channel</button>
  <v-dialog v-model="deletingChat">
    <div class="removal_modal">
      <p>Are you sure you want to delete channel <strong>{{currentChatName}}</strong>?</p>
      <p>This will delete all chat history. This operation is irreversible.</p>
      <v-btn @click="deleteChat">Delete</v-btn>
      <v-btn @click="deletingChat = false">Cancel</v-btn>
    </div>
  </v-dialog>
  </div>
</template>

<script>
  import axios from 'axios'
  import UserPicker from '../Shared/UserPicker'
  export default{
    components: {
      'UserPicker': UserPicker
    },
    data () {
      return {
        recentChats: 'Members',
        adding_new_member: false,
        members: [],
        ownerOfChat: true,
        currentChatName: this.$store.getters.currentChatName,
        deletingChat: false
      }
    },
    created () {
    },
    watch: {
      '$store.getters.members' (newId, oldId) {
        if (newId !== undefined) {
          this.members = this.$store.getters.members
        }
      },
      '$store.getters.currentChatName' (newId, oldId) {
        if (newId !== undefined) {
          this.currentChatName = this.$store.getters.currentChatName
        }
      },
      '$store.getters.currentCreatedByUserId' (newId, oldId) {
        if (newId !== undefined) {
          this.currentCreatedByUserId = this.$store.getters.currentCreatedByUserId
          if (this.currentCreatedByUserId === this.$store.getters.user.id) {
            this.ownerOfChat = true
          } else {
            this.ownerOfChat = false
          }
        }
      }
    },
    computed: {
    },
    methods: {
      async addMember (member) {

          this.$store.dispatch('addMember', { newMember: member.id, roomId: this.$store.getters.currentChatId }).then((value) => {
              this.$store.dispatch('loadChats', { userId: this.$store.getters.user.id })
          })
      },
      deleteChat () {
        axios.get(this.$store.getters.serverHost + '/rest/channel/chat_id/' + this.$store.getters.currentChatId,
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
        ).then(response => {
          this.deletingChat = false
          // delete from mysql db
          if (response.data !== "") {
            axios.delete(this.$store.getters.serverHost + '/rest/channel/' + response.data.channel_id,
              {
                headers: {'idToken': this.$store.getters.user.idToken}
              }
            )
          }

          // delete from firebase
          this.$store.dispatch('deleteChat', { roomId: this.$store.getters.currentChatId }).then((value) => {
            this.$store.dispatch('loadChats', { userId: this.$store.getters.user.id })
            this.$router.push('/chat/' + this.$store.getters.generalRoomId)
          })
        })
      }
    }
  }
</script>
<style>
  .new-member {
    margin-top: 30px;
  }
  .line {
    border-top: 5px solid #e1e1e1;
  }
  .removal_modal {
    background-color: lightblue;
    padding: 1em;
  }

  .delete_button {
    display: block;
    width: 50%;
    margin: 2em auto;
    text-transform: uppercase;
    background-color: red;
    border: 1px solid black;
    color: white;
  }
</style>


