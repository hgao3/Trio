<template>
  <form @submit.prevent="onSignOut">
  <v-btn type="submit">Logout</v-btn>
  </form>
</template>

<script>
  export default {
    computed: {
      user () {
        return this.$store.getters.user
      }
    },
    watch: {
      user (value) {
        if (value == null || value == undefined) {
          this.$router.push('/')
        }
      }
    },
    methods: {
      onSignOut () {
        this.$store.dispatch('signUserOut')
      }
    }
  }
</script>
