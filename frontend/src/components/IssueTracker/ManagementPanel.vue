<template>
  <div>
    <div>

      <div v-if="managerMode" class="management_panel2">
        <div v-if="teammates.length > 0 || managers.length > 0" class="user_list">
          <h2>Project Team</h2>
          <div v-for="manager in managers" :key="manager.email" class="staffing">
            <label>Manager</label>

            <user-icon-2 :user="manager"></user-icon-2>
          </div>
          <br>
          <label>Clients ({{clients.length}})</label>
          <div v-for="client in clients" :key="client.email" class="staffing">
            <user-icon-2 :user="client"></user-icon-2>
            <v-icon v-if="managerMode" class="button" title="Remove client" @click="confirmTeammateRemoval(client)">
              remove_circle</v-icon>
          </div>
          <div class="add_client_button" v-if="managerMode">
            <em v-if="!adding_client">Add new teammates</em>
            <v-icon v-if="!adding_client" @click="adding_client = true" class="button" title="Add new client">
              add_circle
            </v-icon>
            <user-picker v-if="adding_client" :exclusions="managers.concat(teammates).concat(clients)" @pick-user="addClient"
                         @cancel-pick="adding_client = false">
            </user-picker>
          </div>

          <v-dialog v-model="confirm_client_removal" v-if="client_to_remove">
            <div class="removal_modal">
              <p>Are you sure you want to remove <strong>{{client_to_remove.first_name}} {{client_to_remove.last_name}}
                ({{client_to_remove.email}})</strong> from project <strong>{{project.project_title}}</strong>?</p>
              <p>All Issues assigned to {{client_to_remove.first_name}} will be reassigned to the project manager.</p>
              <v-btn @click="removeClient">Remove {{client_to_remove.first_name}}</v-btn>
              <v-btn @click="cancelTeammateRemoval">Cancel</v-btn>
            </div>
          </v-dialog>

        </div>
      </div>
    </div>
  </div>

</template>

<script>
  import * as firebase from 'firebase'
  import {AXIOS} from '../Dashboard/http-common.js'
  import axios from 'axios'
  import UserIcon2 from '../Shared/UserIcon2'
  import UserPicker from '../Shared/UserPicker'
  export default {
    name: 'management-panel',
    props: ['project'],
    components: {
      'user-icon-2': UserIcon2,
      'user-picker': UserPicker
    },
    data () {
      return {
        projectId: '',
        managers: [],
        teammates: [],
        clients: [],
        new_stage_title: '',
        edit_mode: false,
        hide_completed_tasks: true,
        confirm_client_removal: false,
        client_to_remove: null,
        adding_client: false,
        transferring_manager: false,
        deleting: false,
        returnProject: {},
        managerMode: false
      }
    },
    mounted () {
      this.fetchProject()
/*      axios.get(this.$store.getters.serverHost + '/rest/project/' + this.project.project_id,
        {
          headers: {'idToken': this.$store.getters.user.idToken}
        }
      ).then(response => {
        this.returnProject = response.data
        //this.teammates = response.data.teammates
        //this.managers = response.data.managers
        this.managerMode = response.data.managers.indexOf(this.$store.getters.user.email) > -1

        for (let email of response.data.managers) {
          axios.get(this.$store.getters.serverHost + '/rest/user/' + email,
            {
              headers: {'idToken': this.$store.getters.user.idToken}
            }
          ).then(response => {
            this.managers.push(response.data)
          })
        }

        for (let email of response.data.teammates) {
          axios.get(this.$store.getters.serverHost + '/rest/user/' + email,
            {
              headers: {'idToken': this.$store.getters.user.idToken}
            }
          ).then(response => {
            this.teammates.push(response.data)
          })
        }

        for (let email of response.data.clients) {
          axios.get(this.$store.getters.serverHost + '/rest/user/' + email,
            {
              headers: {'idToken': this.$store.getters.user.idToken}
            }
          ).then(response => {
            this.clients.push(response.data)
          })
        }
      })*/
    },
    methods: {
      turnOffEditMode () {
        this.edit_mode = false
      },
      fetchProject (){
        axios.get(this.$store.getters.serverHost + '/rest/project/' + this.project.project_id,
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
        ).then(response => {
          this.returnProject = response.data
          this.managerMode = response.data.managers.indexOf(this.$store.getters.user.email) > -1

          for (let email of response.data.managers) {
            axios.get(this.$store.getters.serverHost + '/rest/user/' + email,
              {
                headers: {'idToken': this.$store.getters.user.idToken}
              }
            ).then(response => {
              this.managers.push(response.data)
            })
          }

          for (let email of response.data.teammates) {
            axios.get(this.$store.getters.serverHost + '/rest/user/' + email,
              {
                headers: {'idToken': this.$store.getters.user.idToken}
              }
            ).then(response => {
              this.teammates.push(response.data)
            })
          }

          for (let email of response.data.clients) {
            axios.get(this.$store.getters.serverHost + '/rest/user/' + email,
              {
                headers: {'idToken': this.$store.getters.user.idToken}
              }
            ).then(response => {
              this.clients.push(response.data)
            })
          }
        })
      },
/*      async fetchUsers () {
        const id = this.project.project_id;
        for (let email_list of ['teammates', 'managers']) {
          let list = []
          this[email_list].splice(0, this[email_list].length)
          for (let email of this.returnProject[email_list]) {
            const requestConfig = {headers: {'idToken': this.$store.getters.user.idToken}}
            const response = await AXIOS.get(`/user/email/${email}`, requestConfig)
            let uid = response.data.uid
            const snapshot = await firebase.database().ref('users').child(uid).once('value')
            list.push(snapshot.val())
          }
          if (this.project.project_id === id) {
            this[email_list] = list
          }
        }
      },*/
      confirmTeammateRemoval(teammate) {
        this.client_to_remove = teammate
        this.confirm_client_removal = true
      },
      removeClient () {
        let config = {headers: {idToken: this.$store.getters.user.idToken}}
        let url = `/project/${this.project.project_id}/remove_client/`
        let payload = {client_email: this.client_to_remove.email}
        AXIOS.patch(url, payload, config)
        this.clients.splice(this.clients.indexOf(this.client_to_remove), 1)
        this.cancelTeammateRemoval()
      },
      cancelTeammateRemoval () {
        this.client_to_remove = null
        this.confirm_client_removal = false
      },
      addClient (user) {
        let config = {headers: {idToken: this.$store.getters.user.idToken}}
        let url = `/project/${this.project.project_id}/add_client/`
        let payload = {client_email: user.email}
        AXIOS.patch(url, payload, config)
        user.first_name = user.firstname
        user.last_name = user.lastname
        this.clients.push(user)
      }
    },
    watch: {
      project: function () {
        this.managers = []
        this.teammates = []
        this.clients = []
        this.fetchProject()
      }
/*      returnProject: function () {
        this.fetchUsers()
      }*/
    }
  }
</script>

<style scoped>
  div {
    text-align: left;
    vertical-align: text-top;
  }

  div.new_stage_widget {
    display: inline-block;
    font-style: italic;
  }

  div.new_stage_widget span:hover {
    cursor: pointer;
  }

  div.staffing {
    display: block;
  }

  div.staffing label {
    display: block;
  }

  h1 {
    display: inline-block;
    font-size: 2em;
    font-weight: bold;
  }

  h2 {
    font-size: 1.2em;
    font-weight: bold;
    text-align: center;
  }

  textarea {
    background-color: white;
    font-size: large;
    font-style: normal;
  }

  .management_panel2 {
    text-align: right;
    background-color: #FBF9F8;
    padding: 1em;
    border-radius: 5px;

  }

  .management_panel div {
    text-align: right;
  }

  .user_list {
    border-bottom: 1px solid black;
    margin-bottom: 1em;
  }

  .add_client_button {
    display: block;
    margin-left: auto;
    margin-right: auto;
    text-align: center;
  }

  .button:hover {
    cursor: pointer;
  }

  .removal_modal {
    background-color: lightblue;
    padding: 1em;
  }

  .delete_button {
    display: block;
    width: 50%;
    margin: 2em auto;
    text-transform: uppercase;
    background-color: red;
    border: 1px solid black;
    color: white;
  }


</style>
