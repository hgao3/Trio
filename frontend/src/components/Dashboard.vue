<template>
    <div class="dashboard">
      <trio-header></trio-header>
      <project-summary v-bind:project="project"></project-summary>
      <stage-summary
        v-for="stage in project.stages"
        :key="stage.getID()"
        :stage="stage"
        :project="project"
      >
      </stage-summary>

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import StageSummary from './StageSummary'
  import TrioHeader from './TrioHeader'
  import {ApiWrapper} from "./http-common"

  export default {
      name: 'dashboard',
      components: {
        'project-summary': ProjectSummary,
        'stage-summary': StageSummary,
        'trio-header': TrioHeader
      },
      data: function() {
        return {
          user: {
            first_name: 'Mike',
            last_name: 'Hachey',
            email: 'mhachey@bu.edu',

          },
          project: {
            title: 'Project Title',
            stages: ApiWrapper.getStages(),
            manager: 'mhachey@bu.edu',
            teammates: ['mhachey@bu.edu']

          },

        };
      },
      methods: {},
      computed: {},
      watch: {}
    }
</script>

<style scoped>
  div {
    vertical-align: text-top;
    padding: 0.5em;
    font-size: 10pt;
  }

</style>
