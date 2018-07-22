<template>
  <div class="project_summary">
    <h1>{{ project.project_title }}</h1>
    <div>
      <div v-if="teammates.length > 0 || managers.length > 0" class="user_list">
        <h2>Project Team</h2>
        <div v-for="manager in managers" :key="manager.email" class="staffing">
          <label>Manager</label>
          <user-icon :user="manager"></user-icon>
        </div>
        <label>Teammates</label>
        <div v-for="teammate in teammates" :key="teammate.email" class="staffing">
          <user-icon :user="teammate"></user-icon>
          <v-icon v-if="managerMode">remove_circle</v-icon>
        </div>
        <div class="add_teammate_button">
          <v-icon v-if="managerMode">add_circle</v-icon>
        </div>

      </div>
      <div>
        <stage-summary v-if="project.project_id"
                       v-for="stage_id in project.stages"
                       :key="stage_id"
                       :stage_id="stage_id"
                       :stages="stages"
                       :project="project"
                       :users="teammates.concat(managers)"
                       :managerMode="managerMode"
        >
        </stage-summary>
      </div>
    </div>


    <div v-if="project.project_id && managerMode" class="new_stage_widget">
      <span v-if="!edit_mode" @click="edit_mode = true">Add new stage...</span>
      <textarea v-if="edit_mode" v-model="new_stage_title"></textarea>
      <br>
      <v-btn v-if="edit_mode" @click="createStage">Save</v-btn>
      <img v-if="edit_mode" src="@/assets/x_button.png" height="20" width="20" @click="turnOffEditMode">
    </div>
  </div>

</template>

<script>
    import * as firebase from 'firebase'
    import {AXIOS} from './http-common.js'
    import StageSummary from './StageSummary'
    import UserIcon from '../Shared/UserIcon'
    export default {
      name: "project-summary",
      props: ['project'],
      components: {
        'stage-summary': StageSummary,
        'user-icon': UserIcon
      },
      data: function() {
        return {
          managers: [],
          teammates: [],
          stages: [],
          new_stage_title: "",
          edit_mode: false
        }
      },
      computed: {
        managerMode: function() {
          return this.managers.map(manager => {return manager.email}).indexOf(this.$store.getters.user.email) > -1;
        }
      },
      methods: {
        turnOffEditMode() {
          this.edit_mode = false;
          this.new_stage_title = "";
        },
        async createStage () {
          let postConfig = {headers: {idToken: this.$store.getters.user.idToken}};
          let postObject = {
            title: this.new_stage_title,
            project_id: this.project.project_id
          };
          let response = await AXIOS.post(`/stage`, postObject, postConfig);
          let newStageId = response.data;
          this.project.stages.push(newStageId);
          this.turnOffEditMode();
        },
        async fetchUsers() {
          const id = this.project.project_id;
          for (let email_list of ['teammates', 'managers']) {
            let list = [];
            this[email_list].splice(0, this[email_list].length); // empty out arrays before loading data from new project
            for (let email of this.project[email_list]) {
              const requestConfig = {headers: {'idToken': this.$store.getters.user.idToken}};
              const response = await AXIOS.get(`/user/email/${email}`, requestConfig);
              let uid = response.data.uid;
              const snapshot = await firebase.database().ref('users').child(uid).once('value');
              list.push(snapshot.val());
            }
            if (this.project.project_id === id) {
              this[email_list] = list;
            }
          }
        }
      },
      watch: {
        project: function () {
          this.stages.splice(0, this.stages.length); // empty out stages before loading data from new project
          this.fetchUsers();
        }/*,
        beforeMount: function() {
          this.fetchUsers();
        }*/
      }
    }
</script>

<style scoped>
  div {
    text-align: left;
    vertical-align: text-top;
  }

  .project_summary {
    min-height: 700px;
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

  .user_list {
    min-width: 10%;
    float: right;
    background-color: #FBF9F8;
    padding: 1em;

  }

  .add_teammate_button {
    display: block;
    margin-left: auto;
    margin-right: auto;
    text-align: center;
  }


</style>
