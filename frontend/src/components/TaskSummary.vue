<template>
  <div>

    <div class="summary" @click="showDetails">
      <p>{{ title }}</p>
      <br v-if="task.isOverdue()"/>
      <span v-if="task.isOverdue()" class="overdue_warning">!</span>
    </div>

    <div v-if="details_visible" class="modal">
      <div class="modal_content">
        <img src="../assets/x_button.png" @click="hideDetails" width="20" height="20">
        <textarea class="title" v-model="title"></textarea>
        <label>Due date</label>
        <datepicker v-model="due_date"></datepicker>
        <label>Description</label>
        <textarea v-model="content"></textarea>
        <label>Stage</label>
        <span class="stage">{{ this.stage.getTitle() }}</span>
      </div>
    </div>
  </div>

</template>
<script>
    import {ApiWrapper} from './http-common'
    import Datepicker from 'vuejs-datepicker'
    export default {
        name: 'TaskSummary',
        components: {'datepicker': Datepicker},
        props: ['task', 'stage', 'project'],
        data: function () {
          return {
            details_visible: false,
            title: this.task.getTitle(),
            content: this.task.getContent(),
            due_date: this.task.getDueDate()
          };
        },
        watch: {
          title: function (new_title) {
            this.task.setTitle(new_title);
          },
          content: function(new_content) {
            this.task.setContent(new_content);
          },
          due_date: function(new_date) {
            this.task.setDueDate(new_date);
          }

        },
        beforeUpdate: function() {
          this.title = this.task.getTitle();
          this.content = this.task.getContent();
          this.due_date = this.task.getDueDate();
        },
        methods: {
          hideDetails: function() {
            this.details_visible = false;
          },
          showDetails: function() {
            this.details_visible = true;
          },
          moveTask(newStage) {
            newStage.insertTask(this.task);
            this.stage.removeTask(this.task);
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

</style>
