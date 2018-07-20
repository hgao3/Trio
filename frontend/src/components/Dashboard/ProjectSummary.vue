<template>
  <div>
    <h1>{{ project.project_title }}</h1>
    <div>
      <div v-for="manager in managers" :key="manager.email" class="staffing">
        <label>Manager</label>
        <user-icon :user="manager"></user-icon>
      </div>
      <div v-for="teammate in teammates" :key="teammate.email" class="staffing">
        <label>Teammate</label>
        <user-icon :user="teammate"></user-icon>
      </div>
    </div>

    <stage-summary v-if="project.project_id"
                   v-for="stage_id in project.stages"
                   :key="stage_id"
                   :stage_id="stage_id"
                   :stages="stages"
                   :project="project"
                   :users="teammates.concat(managers)"
                   :managerMode="managerMode"
    >
    </stage-summary>
  </div>

</template>

<script>
    import * as firebase from 'firebase'
    import {AXIOS} from './http-common.js'
    import StageSummary from './StageSummary'
    import UserIcon from '../Shared/UserIcon'
    export default {
      name: "project-summary",
      props: ['project'],
      components: {
        'stage-summary': StageSummary,
        'user-icon': UserIcon
      },
      data: function() {
        return {
          managers: [],
          teammates: [],
          stages: []
        }
      },
      computed: {
        managerMode: function() {
          return this.managers.map(manager => {return manager.email}).indexOf(this.$store.getters.user.email) > -1;
        }
      },
      watch: {
        project: function() {
          this.stages.splice(0, this.stages.length); // empty out stages before loading data from new project
          let that = this;
          for (let email_list of ['teammates', 'managers']) {
            that[email_list].splice(0, that[email_list].length); // empty out arrays before loading data from new project
            for (let email of this.project[email_list]) {
              let requestConfig = { headers: {'idToken': this.$store.getters.user.idToken}};
              AXIOS.get(`/user/email/${email}`, requestConfig)
                .then( function(response) {
                  let uid = response.data.uid;
                  firebase.database().ref('users').child(uid)
                    .on('value', snapshot => { that[email_list].push(snapshot.val()); })
                } )
                .catch( response => { console.log(response); })
            }
          }
        }
      }
    }
</script>

<style scoped>
  div {
    text-align: left;
    vertical-align: text-top;
  }

  div.staffing {
    display: inline-block;
  }

  div.staffing label {
    display: block;
  }

  h1 {
    display: inline-block;
    font-size: 2em;
    font-weight: bold;
  }


</style>
