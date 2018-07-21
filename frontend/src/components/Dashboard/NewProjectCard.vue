<template>
  <div>
    <h1>New Project</h1>
    <br>
    <label> Title:<input type="text" v-model="title" /></label>


  </div>

</template>

<script>
    import * as firebase from 'firebase'
    export default {
      name: "NewProjectCard",
      props: ['open'],
      data: function() {
        return {
          title: "",
          manager_email: "",
          teammates: [],
          users: []
        }
      },
      beforeMount: function() {
        firebase.database().ref('users').on('value', snapshot => {
          let obj = snapshot.val();
          this.users = Object.keys(obj).map(id => { return obj[id];});
        });
      }
    }
</script>

<style scoped>

  h1 {
    font-size: large;
    text-align: center;
  }

</style>
