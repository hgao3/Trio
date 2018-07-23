<template>

    <div class="issuetracker">
      <div>
        <h1>Your Projects</h1>
        <div class="project_selector"
             v-for="project in project_list"
             @click="selectProject(project)"
        >{{ project.project_title }}</div>
      </div>
      <br>
      <issue-table v-bind:project="selected_project"></issue-table>


    </div>

</template>

<script>
  import IssueTable from './IssueTable'
  import axios from 'axios'

  export default {
    name: 'issuetracker',
    components: {
      'issue-table': IssueTable
    },
    data: function () {
      return {
        selected_project: {},
        project_list: []
      }
    },
    mounted () {
      axios.get(this.$store.getters.serverHost + '/rest/project',
        {
          headers: {'idToken': this.$store.getters.user.idToken}
        }
      ).then(response => {
        this.project_list = response.data
        this.project_list.push({project_id: 'All', project_title: 'Select All'})
      })
    },
    methods: {
      selectProject (project) {
        this.selected_project = project
      }
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

  div.issuetracker {
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
    font-size: 2em;
    font-weight: bold;
  }

</style>
