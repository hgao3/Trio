<template>
    <div class="dashboard">
      <project-summary v-bind:project="project"></project-summary>
      <stage-summary
        v-for="stage in project.stages"
        :key="stage.getID()"
        :stage="stage"
        :project="project"
      >
      </stage-summary>
      <div class="new_stage_button" @click="addStage">Add a stage...</div>

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import StageSummary from './StageSummary'
  import {ApiWrapper} from "./http-common"

  export default {
      name: 'dashboard',
      components: {
        'project-summary': ProjectSummary,
        'stage-summary': StageSummary
      },
      data: function() {
        return {
          project: {
            title: 'Trio Project',
            stages: ApiWrapper.getStages(),
            manager: 'mhachey@bu.edu',
            teammates: ['mhachey@bu.edu']

          },

        };
      },
      methods: {
        addStage: function() {
          this.project.stages.push(ApiWrapper.postStage("", []));
        }
      },
      computed: {
        user: function() {
          return this.$store.state.user;
        }
      },
      watch: {}
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

  div.new_stage_button {
    display: inline-block;
    font-style: italic;
    font-family: Calibri, sans-serif;
  }

  .new_stage_button:hover {
    cursor: pointer;
  }

</style>
