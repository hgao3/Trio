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
        <v-text-field
          name="newMember"
          label="newMember"
          id="newMember"
          v-model="newMember"
          type="email"></v-text-field>
        <v-btn v-on:click='addMember'>Add</v-btn>
      </v-flex>
    </v-layout>
  </v-list>
</template>

<script>
  import axios from 'axios'
  export default{
    data () {
      return {
        recentChats: 'Members',
        members: [],
        newMember: ''
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
      async addMember () {
        if (this.newMember !== '') {
          const user = await axios.get(this.$store.getters.serverHost + '/rest/user/email/' + this.newMember,
            {
              headers: {'idToken': this.$store.getters.user.idToken}
            }
          )
          this.$store.dispatch('addMember', { newMember: user.data.uid, roomId: this.$store.getters.currentChatId }).then((value) => {
              this.$store.dispatch('loadChats', { userId: this.$store.getters.user.id })
              this.newMember = ''
          })
        }
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


