<template>
  <div>
    <span v-if="!user._waiting_for_data">{{ [user.first_name[0], user.last_name[0]].join('').toUpperCase() }}</span>
  </div>

</template>

<script>

    export default {
      name: "UserSummary",
      computed: {
        user: function () {
          return this.$store.state.user;
        }
      }
    }
</script>

<style scoped>
  div {
    float: right;
    border: 0;
    margin: 1.25em;
  }

  span {
    background-color: lightblue;
    color: white;
    border-radius: 50%;
    width: 3em;
    height: 3em;
    text-align: center;
    font-size: 1.15em;
    padding: 0.25em;
    margin-bottom: 0.1em;

  }


</style>
