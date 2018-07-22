<template>
  <div id="IssueTable">
    <table-component :show-filter="false" :data="fetchData" ref="table">
      <table-column show="issue_id" label="ID"></table-column>
      <table-column show="title" label="Title"></table-column>
      <table-column show="content" label="Content"></table-column>
      <table-column show="open_status" label="Open Status"></table-column>
      <table-column show="owner_user_email" label="Owner User Email"></table-column>
      <table-column show="priority_level" label="Priority Level"></table-column>
      <table-column show="project_id" label="Project ID"></table-column>
      <table-column show="create_date" label="Create Date" :formatter="formatter"></table-column>
      <table-column show="update_date" label="Update Date" :formatter="formatter"></table-column>
      <table-column show="close_date" label="Close Date" :formatter="formatter"></table-column>
      <table-column show="task_id" label="Task ID"></table-column>
      <table-column label="" :sortable="false" :filterable="false">
        <template slot-scope="row">
          <a @click="editIssue(row.issue_id)">Edit</a>
         <!-- <a :href="`/issue/${row.issue_id}`">Edit</a>-->
        </template>
      </table-column>
    </table-component>
    <v-btn fab dark class="indigo" @click="createIssue()">
      <v-icon dark style="position: fixed;">add</v-icon>
    </v-btn>
  </div>
</template>
<script>
  import axios from 'axios'
  export default {
    name: 'issue-table',
    props: ['project'],
    data () {
      return {
        projectId: ''
      }
    },
    watch: {
      'project' (newId, oldId) {
        this.projectId = newId.project_id
        this.fetchData()
        this.$refs.table.refresh()
      }
    },
    methods: {
      async fetchData () {
        var url = ''
        if (this.projectId === ''){
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
        return response
      },
      editIssue (id) {
        this.$router.push('/issue/' + id)
      },
      formatter (value, rowProperties) {
        if (value !== '') {
          return new Date(value).toISOString().slice(0, 10)
        }
      },
      createIssue () {
        this.$router.push('/createIssue')
      }
    }
  }
</script>

<style>
  a:hover {
    cursor: pointer;
  }
</style>

