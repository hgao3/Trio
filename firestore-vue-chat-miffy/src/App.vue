<template>
  <div>
    <Initialize />
    <ConversationContainer
      v-for="id in convoIds"
      :conversation="conversations[id]"
      :id="id"
      :key="id"
    />
  </div>
</template>

<script>
import Initialize from './Initialize.vue'
import ConversationContainer from './ConversationContainer.vue'

import { mapState } from 'vuex'

export default {
  name: 'app',
  components: {
    Initialize,
    ConversationContainer
  },

  computed: {
    ...mapState({
      conversations: state => state.conversations.all,
      convoIds: state => state.conversations.allIds
    })
  },
}
</script>

<style>
</style>
