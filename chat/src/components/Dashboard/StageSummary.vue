<template>
  <div>
    <textarea class="title" v-model="title"></textarea>
    <task-summary
      v-for="task in stage.tasks"
      :key="task.getID()"
      :task="task"
      :stage="stage"
      :project="project"
    >
    </task-summary>
    <textarea v-if="edit_mode" v-model="new_task_title"></textarea>
    <button v-if="edit_mode" @click="saveTask">Save</button>
    <img v-if="edit_mode" src="@/assets/x_button.png" height="20" width="20" @click="turnOffEditMode">
    <span v-if="!edit_mode" @click="turnOnEditMode">Add a task...</span>


  </div>

</template>

<script>

  import TaskSummary from './TaskSummary'
  import {ApiWrapper} from './http-common'

  export default {
    name: "stage-summary",
    props: ['stage', 'project'],
    data: function () {
      return {
        edit_mode: false,
        new_task_title: '',
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
      saveTask: function () {
        let newTask = ApiWrapper.postTask(this.new_task_title, '', '');
        this.stage.insertTask(newTask);
        this.new_task_title = '';
        this.turnOffEditMode();
      }
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

</style>
