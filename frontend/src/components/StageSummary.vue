<template>
  <div>
    <h4>{{ stage.title }}</h4>
    <task-summary
      v-for="id in stage.tasks"
      v-bind:task_id="id"
    />
    <textarea v-if="edit_mode" v-model="new_task_title"></textarea>
    <button v-if="edit_mode" @click="saveTask">Save</button>
    <span v-if="!edit_mode"  @click="turnOnEditMode">Add a task...</span>


  </div>

</template>

<script>

  import TaskSummary from './TaskSummary'
  import {fakeAPI} from './http-common'

    // placeholder for real API call
    export default {
      name: "stage-summary",
      components: {
          'task-summary': TaskSummary
        },
      data: function() {
          return {
            stage: {title: '', tasks: []},
            edit_mode: false,
            new_task_title: ''
          };
        },
      props: ['stage_id'],
      created: function () {
          this.stage = fakeAPI.getStage(this.stage_id);
        },
      methods: {
          turnOnEditMode: function () {
            this.edit_mode = true;
          },
          turnOffEditMode: function () {
            this.edit_mode = false;
          },
          saveTask: function() {
            let new_task_id = fakeAPI.postTask(this.new_task_title, '', '');
            this.stage.tasks.push(new_task_id);
            fakeAPI.patchStage(this.stage);
            this.stage = fakeAPI.getStage(this.stage_id);
            this.new_task_title = '';
            this.turnOffEditMode();
          }
      }

    }
</script>

<style scoped>
  div {
    display: inline-block;
    border: 1px solid black;
    font-family: Calibri, sans-serif;
    background-color: lightblue;
    text-align: left;
    margin: 0.25em;
    padding: 0.5em;
    border: 0px;
    min-width: 15%;
  }

  textarea {
    display: block;
    width: 100%;
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

</style>
