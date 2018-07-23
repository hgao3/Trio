<template>
  <div class="project_summary">
    <h1>{{ project.project_title }}</h1>
    <div>

      <div v-if="project.project_id" class="management_panel">
        <div v-if="teammates.length > 0 || managers.length > 0" class="user_list">
          <h2>Project Team</h2>
          <div v-for="manager in managers" :key="manager.email" class="staffing">
            <label>Manager</label>
            <user-icon :user="manager"></user-icon>
          </div>
          <label>Teammates</label>
          <div v-for="teammate in teammates" :key="teammate.email" class="staffing">
            <user-icon :user="teammate"></user-icon>
            <v-icon v-if="managerMode" class="button" title="Remove teammate" @click="confirmTeammateRemoval(teammate)">
              remove_circle</v-icon>
          </div>
          <div class="add_teammate_button">
            <v-icon v-if="managerMode" class="button" title="Add new teammate">add_circle</v-icon>
          </div>

          <v-dialog v-model="confirm_teammate_removal" v-if="teammate_to_remove">
            <div class="removal_modal">
              <p>Are you sure you want to remove <strong>{{teammate_to_remove.firstname}} {{teammate_to_remove.lastname}}
                ({{teammate_to_remove.email}})</strong> from project <strong>{{project.project_title}}</strong>?</p>
              <p>All tasks assigned to {{teammate_to_remove.firstname}} will be reassigned to the project manager.</p>
              <v-btn @click="removeTeammate">Remove {{teammate_to_remove.firstname}}</v-btn>
              <v-btn @click="cancelTeammateRemoval">Cancel</v-btn>
            </div>
          </v-dialog>

        </div>

        <h2>Project Settings</h2>
        <label><input type="checkbox" v-model="hide_completed_tasks"> Hide Completed Tasks</label>
      </div>

        <stage-summary v-if="project.project_id"
                       v-for="stage_id in project.stages"
                       :key="stage_id"
                       :stage_id="stage_id"
                       :stages="stages"
                       :project="project"
                       :users="teammates.concat(managers)"
                       :managerMode="managerMode"
                       :hide_completed_tasks="hide_completed_tasks"
        >
        </stage-summary>
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
          edit_mode: false,
          hide_completed_tasks: true,
          confirm_teammate_removal: false,
          teammate_to_remove: null
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
        },
        confirmTeammateRemoval(teammate) {
          this.teammate_to_remove = teammate;
          this.confirm_teammate_removal = true;
        },
        removeTeammate() {
          let config = {headers: {idToken: this.$store.getters.user.idToken}};
          let url = `/project/${this.project.project_id}/remove_teammate/`;
          let payload = {teammate_email: this.teammate_to_remove.email};
          let that = this;
          AXIOS.patch(url, payload, config);
          this.teammates.splice(this.teammates.indexOf(this.teammate_to_remove, 1));
          this.cancelTeammateRemoval();
        },
        cancelTeammateRemoval() {
          this.teammate_to_remove = null;
          this.confirm_teammate_removal = false;
        }
      },
      watch: {
        project: function () {
          this.stages.splice(0, this.stages.length); // empty out stages before loading data from new project
          this.fetchUsers();
        }
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

  .management_panel {
    min-width: 10%;
    float: right;
    background-color: #FBF9F8;
    padding: 1em;

  }

  .user_list {
    border-bottom: 1px solid black;
    margin-bottom: 1em;
  }

  .add_teammate_button {
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


</style>
