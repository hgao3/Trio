<template>
  <div>
    <input type="text" v-model="user_to_add" placeholder="User name or email">
    <v-icon @click="cancel" class="button">cancel</v-icon>
    <div class="match_list" v-if="matches.length > 0">
      <ul>
        <li v-for="user in matches">
          <user-icon :user="user"></user-icon>
          <v-icon class="button" @click="pickUser(user)">add_circle</v-icon>
        </li>
      </ul>
    </div>
  </div>

</template>

<script>
  import * as firebase from 'firebase'
  import UserIcon from './UserIcon'
  export default {
    name: "UserPicker",
    components: {'user-icon': UserIcon},
    props: ['universe', 'exclusions'],
    data() {
      return {
        user_to_add: '',
        users: []
      } },
    beforeMount: async function() {
      if (this.universe) {
        this.users = this.universe;
      }
      else {
        this.users = await this.fetchAllUsers();
      }
    },
    mounted() {
      this.$el.getElementsByTagName('input')[0].focus();
    },
    computed: {
      matches() {
        let input = this.user_to_add.toLowerCase();
        if (input.length === 0) { return []; }
        return this.range().filter(function(user) {
          return (
            user.email.toLowerCase().startsWith(input) ||
            user.firstname.toLowerCase().startsWith(input) ||
            user.lastname.toLowerCase().startsWith(input)
          );
        });
      }
    },
    methods: {
      range() {
        const exclusions = this.exclusions || [];
        const users = this.users;
        return users
          .filter(user => !exclusions.map(excludedUser => excludedUser.email).includes(user.email));
      },
      async fetchAllUsers() {
        const response = await firebase.database().ref('users').once('value');
        const snapshot = response.val();
        return Object.keys(snapshot).map(id => snapshot[id]);
      },
      pickUser(user) {
        this.$emit('pick-user', user);
        this.user_to_add = '';
        this.$el.getElementsByTagName('input')[0].focus();
      },
      cancel() {
        this.user_to_add = "";
        this.$emit('cancel-pick');
      }
    }
  }
</script>

<style scoped>

  div {
    margin-bottom: 1.5em;
  }

  input {
    font-size: 1.10em;
    border-bottom: 2px solid black;
  }

  input:focus {
    outline: none;
  }

  ul {
    list-style: none;
    padding: 0;
  }

  .match_list {
    position: relative;
    background-color: white;
    min-height: 200px;
    z-index: 5;
    padding: 1em;
    text-align: center;
    width: 200px;
    /*right: 5px;*/
  }

  .button:hover {
    cursor: pointer;
  }

</style>
