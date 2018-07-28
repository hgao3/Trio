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
              <form @submit.prevent="onCreate">
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
                      multi-line
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
                    <v-select
                      v-bind:items="availableProject"
                      v-model="project_id"
                      label="Select a Project"
                      item-text="project_name"
                      item-value="project_id"
                      single-line
                      bottom
                    ></v-select>
                  </v-flex>
                </v-layout>
                <v-layout row>
                  <v-flex xs12 class="dateWrapper">
                    <label class="myTitle">Close Date</label>
                    <datepicker v-model="close_date" placeholder="Pick a Close Date" class="datepickerCss" :format="customFormatter"></datepicker>
                  </v-flex>
                </v-layout>
                <v-layout>
                  <v-flex xs12>
                    <v-btn type="submit">Create</v-btn>
                    <v-btn @click="cancel()">Cancel</v-btn>
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
    name: 'CreateIssue',
    components: {
      'datepicker': Datepicker
    },
    props: [
      'id'
    ],
    data () {
      return {
        title: '',
        content: '',
        priority_level: 'Medium',
        items: [
          'High', 'Medium', 'Low'
        ],
        project_id: '',
        close_date: '',
        availableProject: []
      }
    },
    methods: {
      customFormatter (date) {
        return new Date(date).toLocaleString('en-us', {year: 'numeric', month: '2-digit', day: '2-digit'})
      },
      onCreate () {
        if (this.close_date !== '') {
          this.close_date = new Date(this.close_date).toLocaleString('en-us', {year: 'numeric', month: '2-digit', day: '2-digit'})
        }
        axios.post(this.$store.getters.serverHost + '/rest/issue',
          {
            'title': this.title,
            'content': this.content,
            'owner_user_email': this.$store.getters.user.email,
            'priority_level': this.priority_level,
            'project_id': this.project_id,
            'close_date': this.close_date
          },
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
        ).then(response => {
          this.$router.push('/issueTracker/')
        })
      },
      cancel () {
        this.$router.push('/issueTracker/')
      }
    },
    mounted () {
      axios.get(this.$store.getters.serverHost + '/rest/availableProject',
        {
          headers: {'idToken': this.$store.getters.user.idToken}
        }
      ).then(response => {
        this.availableProject = response.data
      })
    }
  }
</script>
<style>
  .dateWrapper {
    border-bottom: 1px solid #949494 !important;
    width: 100%;
    margin-bottom: 20px;
  }
  .myTitle {
    color: rgba(0,0,0,0.54);
  }
  .datepickerCss {
    margin-top: 5px;
  }

</style>
