<template>
  <div>
    <h1>{{ project.project_title }}</h1>
  </div>

</template>

<script>
    export default {
        name: "project-summary",
        props: ['project']
    }
</script>

<style scoped>
  div {
    text-align: left;
  }

  h1 {
    display: inline-block;
    font-size: 2em;
    font-weight: bold;
  }

</style>
