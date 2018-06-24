<template>
  <div>
    <v-container>
      <v-layout row wrap>
        <v-flex xs12 class="text-xs-center">
          <h3>Select a Channel! Or create a new one!</h3>
          // channels previously called chats //-->
          <chats></chats>
        </v-flex>
      </v-layout>
    </v-container>
  </div>
</template>

<script>
  import Chats from './Chat/Chats.vue'
  export default {
    components: {
      'chats': Chats
    }
  }
</script>
