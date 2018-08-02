<template>
  <div v-if="!(hide_completed_tasks && this.task.isCompleted()) && !(only_show_my_tasks && !current_user_is_assigned())">

    <div :class="status">
      <div class="summary" @click="showDetails">
        <p>{{ title }}</p>
        <br v-if="task.isOverdue()"/>
        <span v-if="task.isOverdue()" class="overdue_warning">!</span>
      </div>
    </div>

    <div v-if="details_visible" class="modal">
      <div class="modal_content">
        <img src="@/assets/x_button.png" @click="hideDetails" width="20" height="20">
        <textarea class="title" v-model="title" :readonly="!current_user_editable"></textarea>


        <div class="info_panel">
          <label>Due date</label>
          <datepicker v-if="managerMode" v-model="due_date"></datepicker>
          <input v-else v-model="date_string" readonly>
          <label>Description</label>
          <textarea v-model="content" class="content" :readonly="!current_user_editable"></textarea>
          <label>Stage</label>
          <span class="stage">{{ this.stage.getTitle() }}</span>
          <button v-if="!moving && managerMode"  class="move_button" @click="moving = true">Move</button>
          <div class="move_menu"  v-if="moving">
            <span>Move to:</span>
            <stage-picker v-for="stage in stages"
                          :stage="stage"
                          :key="stage.getID()"
                          @chosen-stage="moveTask">
            </stage-picker>
            <img src="@/assets/x_button.png" @click="moving = false" width="20" height="20" class="cancel_move">
          </div>
        </div>

        <div class="management_panel">
          <label>Assigned to</label>
          <div @click="assigning=!assigning" v-if="!assigning || !managerMode">
            <user-icon v-if="assigned_user" :user="assigned_user"></user-icon>
          </div>
          <br>
          <user-picker v-if="assigning && managerMode"
                       :universe="users"
                       :exclusions="[assigned_user]"
                       @pick-user="assignUser"
                       @cancel-pick="assigning = false"></user-picker>
          <v-btn
            v-if="!task.isReadyForReview() && current_user_editable"
            @click="task.markReady()">
            Mark Ready for Review
          </v-btn>
          <v-btn v-if="task.isReadyForReview() && current_user_editable"
                 @click="task.markNotReady()">
            Remove from Review</v-btn>
          <v-btn v-if="current_user_editable" @click="openChannel">Go to Channel</v-btn>
          <div v-if="managerMode">
            <v-btn v-if="!task.isCompleted()" @click="markCompleted()">Mark Complete</v-btn>
            <v-btn v-if="task.isCompleted()" @click="markIncomplete()">Mark Incomplete</v-btn>
          </div>
        </div>


        <button class="delete_button" color="red" v-if="managerMode" @click="deleteTask">Delete Task</button>
      </div>
    </div>
  </div>

</template>
<script>
    import {ApiWrapper} from './http-common'
    import {AXIOS} from './http-common'
    import Datepicker from 'vuejs-datepicker'
    import TaskStagePicker from './TaskStagePicker'
    import UserIcon from '../Shared/UserIcon'
    import UserPicker from '../Shared/UserPicker'
    import * as firebase from 'firebase'
    export default {
        name: 'TaskSummary',
        components: {
          'datepicker': Datepicker,
          'stage-picker': TaskStagePicker,
          'user-icon': UserIcon,
          'user-picker': UserPicker
        },
        props: ['task_id', 'stage', 'project', 'stages', 'managerMode', 'hide_completed_tasks', 'users',
          'only_show_my_tasks'],
        data: function () {
          return {
            task: ApiWrapper.getTask(this.task_id),
            details_visible: false,
            moving: false,
            chosen_stage: null,
            assigned_user: null,
            assigning: false,
            channel_id: null
          };
        },
        computed: {
          title: {
            set: function (new_title) {
              this.task.setTitle(new_title);
            },
            get: function () {
              return this.task.title;
            }
          },
          content: {
            set(new_content) {
              this.task.setContent(new_content);
            },
            get() {
              return this.task.getContent();
            }
          },
          due_date: {
            set(date) {
              this.task.setDueDate(date);
            },
            get() {
              return this.task.getDueDate();
            }
          },
          status: {
            get() {
              if (this.task.isReadyForReview() && !this.task.isCompleted()) {
                return "ready_for_review";
              }
              if (this.task.isCompleted()) {
                return "completed";
              }
              return null;
            }
          },
          current_user_editable() {
            return this.current_user_is_assigned() || this.managerMode;
          },
          date_string() {
            if (this.due_date === null) {
              return "";
            }
            else {
              return this.due_date.toDateString();
            }
          }
        },
        methods: {
          hideDetails: function() {
            this.assigning = false;
            this.details_visible = false;
          },

          showDetails: function() {
            this.details_visible = true;
          },
          current_user_is_assigned() {
            return this.$store.getters.user.email === this.task.assigned_user_email;
          },
          async moveTask(newStage) {
            this.stage.removeTask(this.task);
            newStage.insertTask(this.task);
            this.moving = false;
          },
          deleteTask() {
            this.stage.removeTask(this.task);
            let config = {headers: {idToken: this.$store.getters.user.idToken}};
            AXIOS.delete(`task/${this.task.getID()}`, config);
          },
          markCompleted() {
            this.task.markCompleted(this.$store.getters.user.email);
            this.hideDetails();
          },
          markIncomplete() {
            this.task.markIncomplete(this.$store.getters.user.email);
          },
          assignUser(user) {
            //this.assigned_user = user;
            if (this.managerMode) {
              this.assigning = false;
              ApiWrapper.setIdToken(this.$store.getters.user.idToken);
              this.task.setAssignedUserEmail(user.email);
            }
          },
          openChannel () {
            let task_id = this.task.task_id;
            //let that = this;
            if (this.channel_id !== null) {
              this.$router.push('/chat/' + this.channel_id);
            }
            else {
              let chatName = 'Task-' + String(task_id) + ': ' + this.title;
              let key = 0;
              this.$store.dispatch('createChat', { chatName: chatName, userId: this.$store.getters.user }).then((value) => {
                key = value;

                AXIOS.post('/channel/',
                  {
                    'chat_id': key,
                    'owner_user_email': this.$store.getters.user.email,
                    'project_id': 0,
                    'task_id': task_id,
                    'issue_id': 0
                  },
                  {
                    headers: {'idToken': this.$store.getters.user.idToken}
                  }
                ).then(response => {
                  this.channel_id = key;
                  console.log(this.channel_id);
                  this.$router.push('/chat/' + key);
                });
              });
            }

          }
        },
      mounted() {
        AXIOS.get('/channel/task_id/' + this.task_id,
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
        ).then(response => {
          if (response.data !== "") {
            this.channel_id = response.data;
          }
        })
      },
      beforeUpdate: function () {
          if (this.task.assigned_user_email.length > 0) {
            let getConfig = {headers: {idToken: this.$store.getters.user.idToken}};
            AXIOS.get(`/user/email/${this.task.assigned_user_email}`, getConfig)
              .then(response => {
                firebase.database().ref('users').child(response.data.uid)
                  .on('value', snapshot => {
                    this.assigned_user = snapshot.val();
                  })
              })
              .catch(response => {
                console.log(response);
              });
          }
        }
    }
</script>

<style scoped>

  div {
    display: block;
    margin: 0;
    border-radius: 3px;
    width: 100%;
    vertical-align: text-top;

  }

  .summary {
    display: block;
    text-align: left;
    background-color: white;
    border: 0;
    margin: 0;
    padding: 0.5em;
    position: relative;
    max-height: 4em;
    overflow: hidden;
  }

  .summary:hover {
    cursor: pointer;
    background-color: lightcyan;
  }

  .overdue_warning {
    position: absolute;
    right: 0.5em;
    bottom: 0.1em;
    font-size: 1.5em;
    color: red;
    font-face: 'Times New Roman', serif;
  }

  .modal {
    position: fixed; /* Stay in place */
    z-index: 9998; /* Sit on top */
    left: 0;
    top: 0;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0,0,0); /* Fallback color */
    background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
  }

  .modal_content {
    background-color: lightblue;
    margin: 5% auto;
    border-radius: 1%;
    border: 1px solid #888;
    width: 45%;
    position: relative;
    min-height: 80%;
    padding: 1em 1em 1em 3.5%;
  }

  .modal_content textarea.title {
    background-color: inherit;
    border: 0;
    width: 80%;
    display: block;
    font-size: 1.75em;
    height: 1.9em;
    resize: none;
    vertical-align: text-top;
  }

  .modal_content textarea.title:focus {
    background-color: white;
  }

  img {
    float: right;
  }

  img:hover {
    cursor: pointer;
  }

  img.cancel_move {
    position: static;
    display: inline-block;
    float: none;
  }

  button {
    background-color: white;
    border: 1px solid black;
    padding: 0.25em;
  }

  button.move_button {
    display: block;
  }

  .modal_content textarea {
    width: 75%;
    border: 0px;
    display: block;
  }

  label {
    font-weight: bold;
    font-size: 1.2em;
    display: block;
    padding-top: 1em;
    padding-bottom: 0;
    margin-bottom: 0;
  }

  .stage {
    font-size: large;
  }

  .delete_button {
    display: block;
    margin-left: auto;
    margin-right: auto;
    font-weight: bold;
    color: white;
    background-color: red;
    text-transform: uppercase;
    font-size: large;
  }

  .ready_for_review {
    border: 3px solid green;
  }

  .completed {
    border: 3px solid blue;
  }

  .info_panel {
    width: 55%;
    display: inline-block;
  }

  .management_panel {
    width: 40%;
    display: inline-block;
  }

  textarea.content {
    height: 200px;
  }

</style>
