<template>
    <div class="dashboard">
      <trio-header></trio-header>
      <project-summary v-bind:project="project" />
      <stage-summary
        v-for="stage in project.stages"
        v-bind:stage="stage"
        v-on:move-task="moveTask">
      </stage-summary>

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import StageSummary from './StageSummary'
  import TrioHeader from './TrioHeader'
  import {fakeAPI} from "./http-common"

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
            title: 'TeamProject',
            stages: fakeAPI.getStages(),
            manager: 'mhachey@bu.edu',
            teammates: ['mhachey@bu.edu']

          },

        };
      },
      methods: {
        moveTask: function (task_id, stage_id) {
          let index = this.project.stages.indexOf(stage_id);
          if (index > - 1) {
            this.project.stages[stage_id].tasks.push(task_id);
          }
        }

      },
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
