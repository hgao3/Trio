<template>
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
</template>

<script>
  //import axios from 'axios'
  import UserPicker from '../Shared/UserPicker'
  export default{
    components: {
      'UserPicker': UserPicker
    },
    data () {
      return {
        recentChats: 'Members',
        adding_new_member: false,
        members: []
      }
    },
    created () {
    },
    watch: {
      '$store.getters.members' (newId, oldId) {
        if (newId !== undefined) {
          this.members = this.$store.getters.members
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
</style>


