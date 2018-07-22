<template>
  <div class="card">
    <h1>New Project</h1>
    <br>
    <label> Title: <input type="text" v-model="title" /></label>
    <div class="add_teammates">
      <label>Teammates:
        <ul>
          <li v-for="teammate in teammates">
            <user-icon :user="teammate"></user-icon>
            <v-icon @click="removeTeammate(teammate)" class="teammate_button">remove_circle_outline</v-icon>
          </li>
        </ul>
        <input type="email" id="email_field" v-model="user_to_add" placeholder="User name or email">
      </label>
      <div v-if="matchingUsers.length > 0" class="matching_user_container">
        <ul>
          <li v-for="teammate in matchingUsers">
            <user-icon :user="teammate"></user-icon>
            <v-icon class="teammate_button" @click="addTeammate(teammate)">add_circle_outline</v-icon>
          </li>
        </ul>
      </div>
    </div>
    <v-btn v-if="title.length > 0" @click="saveProject">Save Project</v-btn>
  </div>

</template>

<script>
    import * as firebase from 'firebase'
    import {AXIOS} from './http-common'
    import UserIcon from '../Shared/UserIcon'
    export default {
      name: "NewProjectCard",
      components: {
        'user-icon': UserIcon
      },
      props: ['open'],
      data: function() {
        return {
          title: "",
          teammates: [],
          users: [],
          user_to_add: "",
          manager: this.$store.getters.user
        }
      },
      computed: {
        matchingUsers() {

          let matches = [];
          if (this.user_to_add.length > 0) {
            let that = this;
            function match(user, input) {
              let matcher = input.toLowerCase();
              return (
                (user.email.toLowerCase().startsWith(matcher)
                || user.firstname.toLowerCase().startsWith(matcher)
                || user.lastname.toLowerCase().startsWith(matcher)
                )
                && user.email !== that.manager.email
                && that.teammates.indexOf(user) === -1
              );
            }
            matches = this.users.filter(user => match(user, this.user_to_add));
          }
          return matches;
        }
      },
      methods: {
        addTeammate(user) {
          if (this.teammates.indexOf(user) === -1) {
            this.teammates.push(user);
            this.user_to_add = "";
            let field = document.getElementById('email_field');
            field.focus();
          }
        },
        removeTeammate(user) {
          let index = this.teammates.indexOf(user);
          if (index > -1) {
            this.teammates.splice(index, 1);
          }
        },
        saveProject() {
          let teammates = this.teammates;
          let postData = {
            title: this.title,
            manager_email: this.manager.email
          };
          let postConfig = {headers: {idToken: this.$store.getters.user.idToken}};
          AXIOS.post('/project', postData, postConfig).then(response => {
            console.log(response);
            let projectId = response.data;
            for (const teammate of teammates) {
              let requestData = {teammate_email: teammate.email};
              let requestConfig = {headers: {idToken: this.$store.getters.user.idToken}};
              AXIOS.patch(`/project/${projectId}/add_teammate`, requestData, requestConfig);
            }
          });
          this.$emit('close-dialog');
        }
      },
      beforeMount: function() {
        firebase.database().ref('users').on('value', snapshot => {
          let obj = snapshot.val();
          this.users = Object.keys(obj).map(id => { return obj[id];});
        });
      }
    }
</script>

<style scoped>

  div {
    padding: 1em;
    text-align: center;
  }

  h1 {
    font-size: 2em;
    text-align: center;
  }

  h2 {
    font-size: 1.25em;
    text-align: center
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
  }

  .teammate_button:hover {
    cursor: pointer;
  }

  .add_teammates {
    background-color: lightgray;
    width: 60%;
    margin: 1em auto;
  }

  .matching_user_container {
    background-color: white;
  }

  .card {
    min-height: 500px;
    background-color: lightblue;
  }

</style>
