<template>
  <v-list subheader>
    <v-list-tile>
      <v-list-tile-content>
        <v-subheader>Channels</v-subheader>
      </v-list-tile-content>
      <v-list-tile-action>
        <v-icon
          class="createButton"
          @click="toCreate">
          add_circle
        </v-icon>
      </v-list-tile-action>
    </v-list-tile>

    <v-list-tile avatar v-for="(chat, index) in chats" v-bind:key="chat.name" @click="" :to="/chat/ + chat.id">
      <v-list-tile-content>
        <v-list-tile-title v-html="chat.name"></v-list-tile-title>
      </v-list-tile-content>
      <v-list-tile-action>
        <v-icon>chat_bubble</v-icon>
      </v-list-tile-action>
    </v-list-tile>
  </v-list>
</template>

<script>
  export default{
    data () {
      return {
        recentChats: 'Channels'
      }
    },
    created () {
      this.$store.dispatch('loadChats', { userId: this.$store.getters.user.id })
    },
    computed: {
      chats () {
        return this.$store.getters.chats
      }
    },
    methods: {
      toCreate () {
        this.$router.push('/create')
      }
    }
  }
</script>
<style>
  .createButton{
   /* margin-right: 20px;*/
  }
  .createButton:hover {
    cursor: pointer;
  }

</style>
