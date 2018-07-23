<template>
    <div class="dashboard">
      <h1>Your Projects</h1>
      <div>
        <div class="project_selector"
             v-for="project in project_list"
             @click="selectProject(project)"
        >{{ project.project_title }}</div>
        <v-icon class="project_adder" @click="dialog = true" title="Create new project">add_circle</v-icon>
      </div>
      <v-dialog v-model="dialog" persistent width="500px">
        <new-project-card :open="dialog" @save-project="initialize" @close-dialog="dialog = false"></new-project-card>
      </v-dialog>
      <br>
      <project-summary v-bind:project="selected_project"></project-summary>

    </div>
</template>

<script>

  import ProjectSummary from './ProjectSummary'
  import NewProjectCard from './NewProjectCard'
  import {ApiWrapper} from "./http-common"
  import {AXIOS} from './http-common'

  export default {
      name: 'dashboard',
      components: {
        'project-summary': ProjectSummary,
        'new-project-card': NewProjectCard
      },
      data: function() {
        return {
          selected_project: {managers: [], teammates: [], project_title: "", stages: [], id: null},
          project_list: [],
          dialog: false
        };
      },
      methods: {
        addStage: function() {
          this.project.stages.push(ApiWrapper.postStage("", []));
        },
        selectProject(project) {
          this.$router.replace(`/dashboard/${project.project_id}`);
          this.initialize();
        },
        async initialize() {
          this.dialog = false;
          ApiWrapper.setIdToken(this.$store.getters.user.idToken);
          let requestConfig = {headers: {'idToken': this.$store.getters.user.idToken}};
          if (this.$route.params.project_id) {
            let response = await AXIOS.get(`project/${this.$route.params.project_id}`, requestConfig);
            //this.selected_project = null;
            this.selected_project = response.data;
          }
          let that = this;
          AXIOS.get(`/project/by_user/${this.$store.getters.user.email}`, requestConfig)
            .then(response => { that.project_list = response.data; });
        }
      },
      computed: {},
      beforeMount: function() {
        this.initialize();
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

  label {
    display: block;
    width: 80%;
    margin: auto;
  }

  h1 {
    display: inline-block;
    font-size: 2em;
    font-weight: bold;
  }

  .project_adder {
    display: inline-block;
  }

  .project_adder:hover {
    cursor: pointer;
  }

</style>
