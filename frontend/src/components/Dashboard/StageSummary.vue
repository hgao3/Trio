<template>
  <div>
    <textarea class="title" v-model="title"></textarea>
    <v-dialog v-if="managerMode" class="deleter" v-model="dialog">
      <img src="@/assets/x_button.png" height="20" width="20" slot="activator">
      <div>
        <h3>Delete Stage</h3>
        <p>
          Are you sure you want to delete the stage <strong>{{title}}?</strong> This will also delete all associated tasks.
        </p>
          <v-btn color="primary" flat @click="deleteStage">Accept</v-btn>
          <v-btn color="primary" flat @click="dialog = false">Cancel</v-btn>
      </div>
    </v-dialog>
    <task-summary
      v-for="task_id in stage.tasks"
      :key="task_id"
      :task_id="task_id"
      :stage="stage"
      :stages="stages"
      :project="project"
      :managerMode="managerMode"
      :hide_completed_tasks="hide_completed_tasks"
    >
    </task-summary>
    <textarea v-if="edit_mode" v-model="new_task_title"></textarea>
    <v-btn v-if="edit_mode" @click="saveTask">Save</v-btn>
    <img v-if="edit_mode" src="@/assets/x_button.png" height="20" width="20" @click="turnOffEditMode">
    <span v-if="!edit_mode" @click="turnOnEditMode">Add a task...</span>


  </div>

</template>

<script>

  import TaskSummary from './TaskSummary'
  import {ApiWrapper} from './http-common'
  import {AXIOS} from './http-common'

  export default {
    name: "stage-summary",
    props: ['stage_id', 'project', 'stages', 'users', 'managerMode', 'hide_completed_tasks'],
    data: function () {
      return {
        edit_mode: false,
        new_task_title: '',
        stage: ApiWrapper.getStage(this.stage_id),
        dialog: false
      }
    },
    components: {
      'task-summary': TaskSummary
    },
    computed: {
      title: {
        set(string) {
          this.stage.setTitle(string);
        },
        get() {
          return this.stage.title;
        }
      }
    },
    methods: {
      turnOnEditMode: function () {
        this.edit_mode = true;
      },

      turnOffEditMode: function () {
        this.new_task_title = '';
        this.edit_mode = false;

      },
      deleteStage (){
        let stageId = this.stage.stage_id;
        let url = `/stage/${stageId}`;
        let requestConfig = {headers: {idToken: this.$store.getters.user.idToken}};
        AXIOS.delete(url, requestConfig);
        this.stages.splice(this.stages.indexOf(this.stage), 1);
        this.project.stages.splice(this.project.stages.indexOf(stageId), 1);
        this.dialog = false;
      },
      async saveTask () {
        let newTaskId = await ApiWrapper.postTask(this.new_task_title, "", "", this.$store.getters.user.email, this.stage.stage_id);
        this.stage.tasks.push(newTaskId);
        this.new_task_title = '';
        this.turnOffEditMode();
      }
    },
    beforeMount: function() {
      this.stages.push(this.stage);
      ApiWrapper.setIdToken(this.$store.getters.user.idToken);
    }

  }
</script>

<style scoped>
  div {
    display: inline-block;
    font-family: Calibri, sans-serif;
    background-color: #c0f1ff;
    text-align: left;
    margin: 0.25em;
    padding: 0.5em;
    border: 0;
    min-width: 15%;
    position: relative;
    border-radius: 5px;
  }

  textarea {
    display: block;
    width: 93%;
    margin-left: auto;
    margin-right: auto;
    border: none;
    padding: 0.5em;
  }

  span {
    display: block;
    font-style: italic;
  }

  span:hover {
    cursor: pointer;
  }

  img {
    display: inline-block;
  }

  img:hover {
    cursor: pointer;
  }

  button {
    margin: 0.5em;
    width: 6em;
    font-size: 1.10em;
    background-color: white;
  }

  textarea {
    background-color: white;
  }

  textarea.title {
    background-color: inherit;
    width: 80%;
    display: block;
    font-size: 1.5em;
    height: 2em;
    resize: none;
    margin: 0;
    padding: 0;
    display: inline-block;
  }

  textarea.title:focus {
    background-color: white;
  }

  .deleter {
    float: right;
  }

</style>
