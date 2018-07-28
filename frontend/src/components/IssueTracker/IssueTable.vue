<template>
  <div id="IssueTable">
    <table-component :show-filter="false" :data="fetchData" ref="table">
      <table-column :sortable="false" :filterable="false" show="issue_id" label="ID"></table-column>
      <table-column :sortable="false" :filterable="false" show="title" label="Title"></table-column>
      <!--<table-column show="content" label="Content"></table-column>-->
<!--      <table-column show="open_status" label="Open Status"></table-column>-->
      <table-column :sortable="false" :filterable="false" show="owner_user_name" label="Owner"></table-column>
      <table-column :sortable="false" :filterable="false" show="priority_level" label="Priority"></table-column>
      <table-column :sortable="false" :filterable="false" show="project_name" label="Project"></table-column>
      <table-column :sortable="false" :filterable="false" show="create_date" label="Create Date"></table-column>
      <table-column :sortable="false" :filterable="false" show="update_date" label="Update Date"></table-column>
<!--      <table-column show="close_date" label="Close Date"></table-column>-->
      <table-column :sortable="false" :filterable="false" show="task_owner_user_name" label="Task Assigned to"></table-column>
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
        return response
      },
      editIssue (id) {
        this.$router.push('/issue/' + id)
      },
/*      formatter (value, rowProperties) {
        if (value !== '') {
          return new Date(value).toISOString().slice(0, 10)
        }
      },*/
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

