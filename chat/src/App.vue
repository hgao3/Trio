<template>
  <v-app>
    <v-navigation-drawer temporary v-model="drawerToggle">
      <v-list>
        <v-list-tile>
          <v-list-tile-action>
            <v-icon>supervisor_account</v-icon>
          </v-list-tile-action>
          <v-list-tile-content>
            Online Users {{onlineUsers[0]}}
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
      <v-list >
        <v-list-tile avatar v-for="user in onlineUsers[1]" @click="">
          <v-list-tile-avatar>
            <img src="../static/icon.jpg" />
          </v-list-tile-avatar>
          <v-list-tile-content>
            <v-list-tile-title>{{user.user}}</v-list-tile-title>
          </v-list-tile-content>
        </v-list-tile>
      </v-list>
    </v-navigation-drawer>
    <v-toolbar class="light-blue darken-1">
      <v-toolbar-side-icon @click.native.stop="drawerToggle = !drawerToggle"></v-toolbar-side-icon>
      <v-toolbar-title>
        <router-link to="/chat/0" tag="span" style="cursor: pointer">Trio Instant Message</router-link>
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-toolbar-items v-for="item in menuItems">
        <v-btn flat :key="item.title" :to="item.route">
          <v-icon left>{{ item.icon }}</v-icon>
          <div class="hidden-xs-only">{{ item.title }}</div>
        </v-btn>
      </v-toolbar-items>
    </v-toolbar>
    <main>
      <router-view></router-view>
    </main>
  </v-app>
</template>

<script>
  export default {
    data () {
      return {
        drawerToggle: false
      }
    },
    computed: {
      menuItems () {
        let items = [
          { icon: 'dvr', title: 'Dashboard', route: ''},
          { icon: 'face', title: 'Register', route: '/register' },
          { icon: 'lock_open', title: 'Login', route: '/login' }
        ]
        if (this.userIsAuthenticated) {
          items = [
            {icon: 'dvr', title: 'Dashboard'},
            {icon: 'chat', title: 'Create a Channel', route: '/create'},
            {icon: 'face', title: 'Users', route: '/UsersTable'}
          ]
        }
        return items
      },
      userIsAuthenticated () {
        return this.$store.getters.user !== null && this.$store.getters.user !== undefined
      },
      onlineUsers () {
        console.log(this.$store.getters.onlineUsers)
        return this.$store.getters.onlineUsers
      }
    }
  }
</script>

<style lang="stylus">
  @import './stylus/main'
</style>
