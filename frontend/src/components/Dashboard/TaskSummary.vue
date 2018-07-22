<template>
  <div>

    <div class="summary" @click="showDetails">
      <p>{{ title }}</p>
      <br v-if="task.isOverdue()"/>
      <span v-if="task.isOverdue()" class="overdue_warning">!</span>
    </div>

    <div v-if="details_visible" class="modal">
      <div class="modal_content">
        <img src="@/assets/x_button.png" @click="hideDetails" width="20" height="20">
        <textarea class="title" v-model="title"></textarea>
        <label>Assigned to</label>
        <user-icon v-if="assigned_user" :user="assigned_user"></user-icon>
        <label>Due date</label>
        <datepicker v-model="due_date"></datepicker>
        <label>Description</label>
        <textarea v-model="content"></textarea>
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
    import * as firebase from 'firebase'
    export default {
        name: 'TaskSummary',
        components: {
          'datepicker': Datepicker,
          'stage-picker': TaskStagePicker,
          'user-icon': UserIcon
        },
        props: ['task_id', 'stage', 'project', 'stages', 'managerMode'],
        data: function () {
          return {
            task: ApiWrapper.getTask(this.task_id),
            details_visible: false,
            moving: false,
            chosen_stage: null,
            assigned_user: null
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
          }
        },
        methods: {
          hideDetails: function() {
            this.details_visible = false;
          },

          showDetails: function() {
            this.details_visible = true;
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
          }
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
  }

  .summary {
    display: block;
    text-align: left;
    background-color: white;
    border: 0px;
    padding: 0.5em;
    position: relative;
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
    width: 80%;
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

</style>
