<template>
  <div>
    <a href="/">
      <img src="../assets/trio_logo_white.png">
    </a>
      <user-summary></user-summary>
  </div>

</template>

<script>
  import UserSummary from './UserSummary'
  export default {
    name: "TrioHeader",
    components: {
      'user-summary': UserSummary
    },
  }
</script>

<style scoped>
  div {
    background-color: #c0f1ff;
    border-bottom: 1px solid #8bafbd;
    text-align: center;
  }

  a {
    padding: 1em;
  }

  img {
    width: 160px;
    height: 44px;
    display: inline-block;
    padding: 0;
  }

</style>
