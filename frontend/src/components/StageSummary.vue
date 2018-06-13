<template>
  <div>
    <h4>{{ stage.title }}</h4>
    <task-summary
      v-for="task_id in stage.tasks"
      v-bind:task_id="task_id"
      v-bind:stage="stage"
      v-on:move-task="moveTask"
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
      data: function() {
        return {
          edit_mode: false,
          new_task_title: ''
        }
      },
      components: {
          'task-summary': TaskSummary
        },
      props: ['stage'],
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
            this.new_task_title = '';
            this.turnOffEditMode();
          },
        moveTask: function (task_id, stage_id) {
            let index = this.stage.tasks.indexOf(task_id);
            if (index > - 1) {
              this.stages.tasks.splice(task_id);
            }
            this.$emit('move-task', task_id, stage_id);
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

</style>
