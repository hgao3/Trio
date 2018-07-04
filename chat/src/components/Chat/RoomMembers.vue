<template>
  <v-list subheader>
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
      addMember () {
        if (this.newMember !== '') {
          this.$store.dispatch('addMember', { newMember: this.newMember, roomId: this.$store.getters.currentChatId }).then((value) => {
            value
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
</style>
