<template>
  <div id="app">
    <table-component :data="fetchData">
      <table-column show="user_id" label="ID"></table-column>
      <table-column show="first_name" label="First name"></table-column>
      <table-column show="last_name" label="Last name"></table-column>
      <table-column show="email" label="Email"></table-column>
      <table-column show="is_admin" label="Is admin"></table-column>
    </table-component>
  </div>
</template>

<script>
  import axios from 'axios'
  export default {
    methods: {
      async fetchData ({ page, filter, sort }) {
        const response = await axios.get('http://localhost:8088/rest/user',
          {
            headers: {'idToken': this.$store.getters.user.idToken}
          }
          )

        // An object that has a `data` and an optional `pagination` property
        return response
      }
    }
  }
</script>

