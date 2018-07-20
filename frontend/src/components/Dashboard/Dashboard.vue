<template>
    <div class="dashboard">
      <h1>Your Projects</h1>
      <div>
        <div class="project_selector"
             v-for="project in project_list"
             @click="selectProject(project)"
        >{{ project.project_title }}</div>
      </div>
      <br>
      <project-summary v-bind:project="selected_project"></project-summary>

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import {ApiWrapper} from "./http-common"
  import {AXIOS} from './http-common'

  export default {
      name: 'dashboard',
      components: {
        'project-summary': ProjectSummary
      },
      data: function() {
        return {
          selected_project: {managers: [], teammates: [], project_title: "", stages: [], id: null},
          project_list: []
        };
      },
      methods: {
        addStage: function() {
          this.project.stages.push(ApiWrapper.postStage("", []));
        },
        selectProject(project) {
          this.selected_project = project;
        },
      },
      computed: {},
      beforeCreate: function() {
        ApiWrapper.setIdToken(this.$store.getters.user.idToken);
        AXIOS.get(`/project/by_user/${this.$store.getters.user.email}`, {headers: {'idToken': this.$store.getters.user.idToken}})
          .then( response => {
          this.project_list = response.data;
        } )
      }
    }
</script>

<style scoped>
  div {
    font-size: 10pt;
    padding: 0;
    margin: 0;
    background-color: lightblue;
  }

  div.dashboard {
    padding: 1em;
  }

  div.project_selector {
    display: inline-block;
    background-color: white;
    padding: 0.5em;
    text-align: center;
    margin: 0.5em 0.5em 0.5em 0;
    border: 0.25em solid #c0f1ff;
  }

  div.project_selector:hover {
    cursor: pointer;
  }

  h1 {
    display: inline-block;
    font-size: 2em;
    font-weight: bold;
  }

</style>
