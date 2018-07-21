<template>

    <div class="issuetracker">
      <div>
        <div class="project_selector"
             v-for="project in project_list"
             @click="selectProject(project)"
        >{{ project.project_title }}</div>
      </div>
      <br>
      <project-summary v-bind:project="selected_project"></project-summary>
      <issue-table v-bind:project="selected_project"></issue-table>


    </div>

</template>

<script>
  import ProjectSummary from '../Dashboard/ProjectSummary'
  import IssueTable from './IssueTable'
  import {ApiWrapper} from '../Dashboard/http-common'
  import {AXIOS} from '../Dashboard/http-common'

  export default {
    name: 'issuetracker',
    components: {
      'project-summary': ProjectSummary,
      'issue-table': IssueTable
    },
    data: function() {
      return {
        selected_project: { stages: [], project_id: '', project_title: ''},
        project_list: [],
        user: ApiWrapper.getUser(this.$store.getters.user.id),
        stages: []
      }
    },
    methods: {
      addStage: function () {
        this.project.stages.push(ApiWrapper.postStage('', []))
      },
      selectProject (project) {
        this.selected_project = project
      }
    },
    computed: {},
    beforeCreate: function() {
      ApiWrapper.setIdToken(this.$store.getters.user.idToken);
      AXIOS.get('/project', {headers: {'idToken': this.$store.getters.user.idToken}}).then( response => {
        this.project_list = response.data
      })
    }
  }

</script>

<style scoped>
  div {
    vertical-align: text-top;
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

  div.new_stage_button {
    display: inline-block;
    font-style: italic;
    font-family: Calibri, sans-serif;
  }

  .new_stage_button:hover {
    cursor: pointer;
  }

</style>
