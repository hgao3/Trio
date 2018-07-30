<template>
  <div>
    <div id="IssueTable">
      <table-component :show-filter="false" :data="fetchData" ref="table">
        <table-column :sortable="false" :filterable="false" show="issue_id" label="ID"></table-column>
        <table-column :sortable="false" :filterable="false" show="title" label="Title"></table-column>
        <!--<table-column show="content" label="Content"></table-column>-->
  <!--      <table-column show="open_status" label="Open Status"></table-column>-->
        <table-column :sortable="false" :filterable="false" show="owner_user_name" label="Owner"></table-column>
        <table-column :sortable="false" :filterable="false" show="priority_level" label="Priority"></table-column>
        <table-column :sortable="false" :filterable="false" show="project_title" label="Project"></table-column>
        <table-column :sortable="false" :filterable="false" show="create_date" label="Create Date"></table-column>
        <table-column :sortable="false" :filterable="false" show="update_date" label="Update Date"></table-column>
  <!--      <table-column show="close_date" label="Close Date"></table-column>-->
        <table-column :sortable="false" :filterable="false" show="task_owner_user_name" label="Task Assigned to" >
          <template slot-scope="row">
            <div v-if="row.task_owner_user_name !==''" >{{row.task_owner_user_name}}</div>
            <a v-if="row.task_owner_user_name ==='' && issueManagerMode" @click="convertToTask(row.issue_id)">Convert to Task</a>

          </template>
        </table-column>
        <table-column label="Edit" :sortable="false" :filterable="false">
          <template slot-scope="row"><a @click="editIssue(row.issue_id)">Edit</a></template>
        </table-column>
      </table-component>
      <v-btn v-if="canAdd" fab dark class="indigo" @click="createIssue()">
        <v-icon dark style="position: fixed;">add</v-icon>
      </v-btn>
    </div>
  </div>
</template>
<script>
  import axios from 'axios'
  import ManagementPanel from './ManagementPanel'
  export default {
    name: 'issue-table',
    props: ['project'],
    data () {
      return {
        projectId: this.$store.getters.issueSelectProject.project_id,
        issueManagerMode: this.$store.getters.issueManagerMode,
        canAdd: false
      }
    },
    watch: {
      'project' (newId, oldId) {
        this.projectId = newId.project_id
        this.issueManagerMode = false
        this.fetchData()
        this.$refs.table.refresh()
      },
      '$store.getters.issueManagerMode' (newId, oldId) {
        if (newId !== undefined) {
          this.issueManagerMode = this.$store.getters.issueManagerMode
        }
      }
    },
    methods: {
      async fetchData () {
        var url = ''
        if (this.projectId === 'All' || this.projectId === '') {
          url = this.$store.getters.serverHost + '/rest/issue'
        } else {
          url = this.$store.getters.serverHost + '/rest/issue/project_id/' + parseInt(this.projectId)
        }
        const response = await axios.get(url,
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
        )

        // An object that has a `data` and an optional `pagination` property
        if (response.data && response.data.length === 0) {
          this.canAdd = false
        } else {
          this.canAdd = true
        }
        return response
      },
      editIssue (id) {
        this.$router.push('/issue/' + id)
      },
      formatter (value, rowProperties) {
        if (value !== '') {
          return value
        } else {
          return `<a @click='convertToTask(rowProperties.issue_id)'>Convert to Task</a>`
        }
      },
      createIssue () {
        this.$router.push('/createIssue')
      },
      convertToTask (issueId) {
        axios.post(this.$store.getters.serverHost + '/rest/issue/convertToTask/' + issueId,
          {},
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
        ).then(response => {
          this.fetchData()
          this.$refs.table.refresh()
        })
      }
    }
  }
</script>

<style>
  a:hover {
    cursor: pointer;
  }
</style>

