<template>
    <div class="dashboard">
      <div>
        <div class="project_selector"
             v-for="project in project_list"
             @click="selectProject(project)"
        >{{ project.project_title }}</div>
      </div>
      <br>
      <project-summary v-bind:project="selected_project"></project-summary>
      <stage-summary v-if="selected_project.project_id"
        v-for="stage_id in selected_project.stages"
        :key="stage_id"
        :stage_id="stage_id"
         :stages="stages"
        :project="selected_project"
      >
      </stage-summary>
      <!--<div class="new_stage_button" @click="addStage" v-if="selected_project.project_id">Add a stage...</div> -->

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import StageSummary from './StageSummary'
  import {ApiWrapper} from "./http-common"
  import {AXIOS} from './http-common'

  export default {
      name: 'dashboard',
      components: {
        'project-summary': ProjectSummary,
        'stage-summary': StageSummary
      },
      data: function() {
        return {
          selected_project: { stages: [], project_id: '', project_title: ''},
          project_list: [],
          user: ApiWrapper.getUser(this.$store.getters.user.id),
          stages: []
        };
      },
      methods: {
        addStage: function() {
          this.project.stages.push(ApiWrapper.postStage("", []));
        },
        selectProject(project) {
          this.selected_project = project;
        }
      },
      computed: {},
      beforeCreate: function() {
        ApiWrapper.setIdToken(this.$store.getters.user.idToken);
        AXIOS.get('/project', {headers: {'idToken': this.$store.getters.user.idToken}}).then( response => {
          this.project_list = response.data;
        } )
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
