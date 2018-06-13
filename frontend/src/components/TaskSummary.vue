<template>
  <div>

    <div class="summary" @click="showDetails">
      <p>{{ task.title }}</p>
      <br v-if="overdue"/>
      <span v-if="overdue">!</span>
    </div>

    <div v-if="details_visible" class="modal">
      <div class="modal_content">
        <img src="../assets/x_button.png" @click="hideDetails" width="20" height="20">
        <h2>{{ task.title }}</h2>
        <p><strong>Due date:</strong>{{ task.due_date}} </p>
        <label>Description</label>
        <textarea v-model="task.content"></textarea>
      </div>
    </div>

  </div>

</template>

<script>
    import {fakeAPI} from './http-common'
    //import TaskDetails from './TaskDetails'
    export default {
        name: "TaskSummary",
        //components: {'task-details': TaskDetails},
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
        },
        methods: {
          hideDetails: function() {
            this.details_visible = false;
          },
          showDetails: function() {
            this.details_visible = true;
          }
        }
    }
</script>

<style scoped>

  div {
    display: block;
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
  }

  span {
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
    margin-left: auto;
    margin-right: auto;
    padding: 1em;
    border: 1px solid #888;
    width: 50%;
    position: relative;
    min-height: 100%;
  }

  img {
    float: right;
  }

  .modal_content textarea {
    width: 100%;
    border: 0px;
  }

</style>
