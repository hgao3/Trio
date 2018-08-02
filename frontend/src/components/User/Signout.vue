<template>
  <v-container>
    <v-layout row>
      <v-flex xs12 sm6 offset-sm3>
        <v-card>
          <v-card-text>
            <v-container>
              <form @submit.prevent="onSignOut">
                <v-layout row>
                  <v-flex xs12>
                    <v-subheader>Are you sure you want to log out?</v-subheader>
                  </v-flex>
                </v-layout>
                <v-layout>
                  <v-flex xs12>
                    <v-btn type="submit">Logout</v-btn>
                  </v-flex>
                </v-layout>
              </form>
            </v-container>
          </v-card-text>
        </v-card>
      </v-flex>
    </v-layout>
  </v-container>
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
