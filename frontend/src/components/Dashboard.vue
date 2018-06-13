<template>
    <div class="dashboard">
      <trio-header></trio-header>
      <project-summary v-bind:project="project" />
      <stage-summary
        v-for="id in project.stages"
        v-bind:stage_id="id">
      </stage-summary>

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import StageSummary from './StageSummary'
  import TrioHeader from './TrioHeader'
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
            stages: [0, 1, 2, 3, 4],
            manager: 'mhachey@bu.edu',
            teammates: ['mhachey@bu.edu']

          },

        };
      },
      methods: {

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
