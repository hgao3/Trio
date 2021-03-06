<template>
  <div class="project_summary">
    <textarea class="project_title" v-bind:readonly="!managerMode" v-model="project_title"></textarea>
    <div>

      <div v-if="project.project_id" class="management_panel">
        <div v-if="teammates.length > 0 || managers.length > 0" class="user_list">
          <h2>Project Team</h2>
          <div v-for="manager in managers" :key="manager.email" class="staffing">
            <label>Manager</label>
            <div v-if="!transferring_manager || !managerMode" @click="transferring_manager = !transferring_manager">
              <user-icon :user="manager"></user-icon>
            </div>
            <user-picker v-if="managerMode && transferring_manager" :universe="teammates" :exclusions="managers"
              @pick-user="transferManager" @cancel-pick="transferring_manager = false"></user-picker>
          </div>
          <br>
          <label>Teammates ({{teammates.length}})</label>
          <div v-for="teammate in teammates" :key="teammate.email" class="staffing">
            <user-icon :user="teammate"></user-icon>
            <v-icon v-if="managerMode" class="button" title="Remove teammate" @click="confirmTeammateRemoval(teammate)">
              remove_circle</v-icon>
          </div>
          <div class="add_teammate_button" v-if="managerMode">
            <em v-if="!adding_teammate">Add new teammates</em>
            <v-icon v-if="!adding_teammate" @click="adding_teammate = true" class="button" title="Add new teammate">
              add_circle
            </v-icon>
            <user-picker v-if="adding_teammate" :exclusions="managers.concat(teammates).concat(clients)" @pick-user="addTeammate"
            @cancel-pick="adding_teammate = false">
            </user-picker>
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
        <br>

        <h2>Project Settings</h2>
        <label><input type="checkbox" v-model="hide_completed_tasks"> Hide Completed Tasks</label>
        <label><input type="checkbox" v-model="only_show_my_tasks"> Only Show My Tasks</label>
        <v-btn v-if="(!hide_channel_button) && project.project_id" @click="openChannel">Go to Project Channel</v-btn>
        <button class="delete_button" v-if="managerMode" @click="deleting = true" color="warning">Delete Project</button>
        <v-dialog v-model="deleting">
          <div class="removal_modal">
            <p>Are you sure you want to delete project <strong>{{project.project_title}}</strong>?</p>
            <p>This will delete all tasks and stages. This operation is irreversible.</p>
            <v-btn @click="deleteProject">Delete</v-btn>
            <v-btn @click="deleting = false">Cancel</v-btn>
          </div>
        </v-dialog>
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
                       :only_show_my_tasks="only_show_my_tasks"
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
    import UserPicker from '../Shared/UserPicker'
    export default {
      name: "project-summary",
      props: ['project'],
      components: {
        'stage-summary': StageSummary,
        'user-icon': UserIcon,
        'user-picker': UserPicker
      },
      data: function() {
        return {
          managers: [],
          teammates: [],
          clients: [],
          stages: [],
          new_stage_title: "",
          edit_mode: false,
          hide_completed_tasks: true,
          only_show_my_tasks: false,
          confirm_teammate_removal: false,
          teammate_to_remove: null,
          adding_teammate: false,
          transferring_manager: false,
          deleting: false,
          channel_id: null,
          hide_channel_button: false
        }
      },
      computed: {
        managerMode: function() {
          return this.managers.map(manager => {return manager.email}).indexOf(this.$store.getters.user.email) > -1;
        },
        project_title: {
          set(title) {
            this.project.project_title = title;
            let manager_email = this.managers[0].email;
            let config = {headers: {idToken: this.$store.getters.user.idToken }};
            let patch = Object.assign(this.project, {title: title, manager_email: manager_email});
            AXIOS.patch(`/project/${this.project.project_id}`, patch, config);

          },
          get() {
            return this.project.project_title;
          }
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
          for (let email_list of ['clients', 'teammates', 'managers']) {
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
          AXIOS.patch(url, payload, config);
          this.teammates.splice(this.teammates.indexOf(this.teammate_to_remove), 1);
          this.cancelTeammateRemoval();
        },
        cancelTeammateRemoval() {
          this.teammate_to_remove = null;
          this.confirm_teammate_removal = false;
        },
        addTeammate(user) {
          let config = {headers: {idToken: this.$store.getters.user.idToken}};
          let url = `/project/${this.project.project_id}/add_teammate/`;
          let payload = {teammate_email: user.email};
          AXIOS.patch(url, payload, config);
          this.teammates.push(user);
        },
        transferManager(user) {
          this.teammates.splice(this.teammates.indexOf(user), 1);
          const patch_object = {title: this.project.project_title, manager_email: user.email};
          const url = `/project/${this.project.project_id}`;
          const config = {headers: {idToken: this.$store.getters.user.idToken}};
          this.project.managers.pop();
          this.project.managers.push(user.email);
          AXIOS.patch(url, patch_object, config);
          const previousManager = this.managers.pop();
          this.managers.push(user);
          this.teammates.push(previousManager);
          this.transferring_manager = false;
        },
        deleteProject() {
          this.deleting = false;
          this.$emit('deleted', this.project);
        },
        openChannel () {
          let project_id = this.project.project_id;
          //let that = this;
          if (this.channel_id !== null) {
            this.$router.push('/chat/' + this.channel_id);
          }
          else {
            let chatName = 'Project-' + String(project_id) + ': ' + this.project_title;
            let key = 0;
            this.$store.dispatch('createChat', { chatName: chatName, userId: this.$store.getters.user }).then((value) => {
              key = value;

              AXIOS.post('/channel/',
                {
                  'chat_id': key,
                  'owner_user_email': this.$store.getters.user.email,
                  'project_id': project_id,
                  'task_id': 0,
                  'issue_id': 0
                },
                {
                  headers: {'idToken': this.$store.getters.user.idToken}
                }
              ).then(response => {
                this.channel_id = key;
                this.$router.push('/chat/' + key);
              });
            });
          }

        }
      },
      watch: {
        project: function () {
          this.channel_id = null;
          this.hide_channel_button = true;
          this.stages.splice(0, this.stages.length); // empty out stages before loading data from new project
          AXIOS.get('/channel/project_id/' + this.project.project_id,
            {
              headers: {'idToken': this.$store.getters.user.idToken}
            }
          ).then(response => {
            this.hide_channel_button = false;
            if (response.data !== "") {
              this.channel_id = response.data;
            }
          });
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

  textarea.project_title {
    display: block;
    font-size: 2em;
    height: 2.1em;
    font-weight: bold;
    background-color: rgba(0, 0, 0, 0);
    resize: none;
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
    text-align: right;
    width: 18%;
    float: right;
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

  .delete_button {
    display: block;
    width: 50%;
    margin: 2em auto;
    text-transform: uppercase;
    background-color: red;
    border: 1px solid black;
    color: white;
  }

  label {
    display: block;
  }


</style>
