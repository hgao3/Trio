<template>
  <v-container>
    <v-layout row v-if="error">
      <v-flex xs12 sm6 offset-sm3>
        <app-alert @dismissed="onDismissed" :text="error.message"></app-alert>
      </v-flex>
    </v-layout>
    <v-layout row>
      <v-flex xs12 sm6 offset-sm3>
        <v-card>
          <v-card-text>
            <v-container>
              <form @submit.prevent="onUpdate">
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="issue_id"
                      label="ID"
                      id="issue_id"
                      v-model="issue_id"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="title"
                      label="Title"
                      id="title"
                      v-model="title"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="content"
                      label="Content"
                      id="content"
                      v-model="content"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-select
                      v-bind:items="items2"
                      v-model="open_status"
                      label="Select a Open Status"
                      single-line
                      bottom
                    ></v-select>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="owner_user_email"
                      label="Owner User Email"
                      id="owner_user_email"
                      v-model="owner_user_email"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-select
                      v-bind:items="items"
                      v-model="priority_level"
                      label="Select a Priority Level"
                      single-line
                      bottom
                    ></v-select>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="project_id"
                      label="Project ID"
                      id="project_id"
                      v-model="project_id"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12 class="dateWrapper">
                    <datepicker v-model="create_date" placeholder="Pick a Create Date"></datepicker>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="update_date"
                      label="Update Date"
                      id="update_date"
                      v-model="update_date"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="close_date"
                      label="Close Date"
                      id="close_date"
                      v-model="close_date"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12>
                    <v-text-field
                      name="task_id"
                      label="Task ID"
                      id="task_id"
                      v-model="task_id"
                      type="text"
                      required></v-text-field>
                  </v-flex>
                </v-layout>
                <v-layout>
                  <v-flex xs12>
                    <v-btn type="submit">Sumbit</v-btn>
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
  import axios from 'axios'
  import Datepicker from 'vuejs-datepicker'
  export default {
    name: "Issue.vue",
    components: {
      'datepicker': Datepicker
    },
    data () {
      return {
        issue_id: '',
        title: '',
        content: '',
        open_status: 'Open',
        items2: [
          'Open', 'Close'
        ],
        owner_user_email: '',
        priority_level: 'Medium',
        items: [
          'Critical', 'High', 'Medium', 'Low'
        ],
        project_id: '',
        create_date: '',
        update_date: '',
        close_date: '',
        task_id: ''
      }
    },
    methods: {
      onUpdate () {
        const qs = require('qs')
        axios.post(this.$store.getters.serverHost + '/signup', qs.stringify(
          {
            'first_name': this.firstname,
            'last_name': this.lastname,
            'email': this.email
          }
        ))
      }
    }
  }
</script>
<style>
  .dateWrapper {
    height: 40px;
    /* border: 1px; */
    border: 1px solid #ccc !important;
    width: 100%;
  }

</style>
