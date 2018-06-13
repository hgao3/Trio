<template>
  <div @click="details_visible = true">
    <p>{{ task.title }}</p>
    <span v-if="overdue">!</span>
    <task-details v-if="details_visible" v-bind:task="task" v-on:hide-details="details_visible = false"></task-details>
  </div>

</template>

<script>
    import {fakeAPI} from './http-common'
    import TaskDetails from './TaskDetails'
    export default {
        name: "TaskSummary",
        components: {'task-details': TaskDetails},
        props: ['task_id'],
        data: function () {
          return {
            task: {
              title: '',
              content: '',
              due_date: ''
            },
            details_visible: false
          }
        },
        computed: {
          overdue: function() {
            return Date.parse(this.task.due_date) <= new Date();
          }
        },
        created: function() {
          this.task = fakeAPI.getTask(this.task_id);
        }
    }
</script>

<style scoped>
  div {
    display: block;
    text-align: left;
    background-color: white;
    border: 0px;
    padding: 0.5em;
    position: relative;
  }

  div:hover {
    cursor: pointer;
  }

  span {
    justify-content: right;
    font-size: 2em;
    color: red;
    font-face: 'Times New Roman', serif;
  }

</style>
