<template>
  <div>
    <h1>{{ project.title }}</h1>
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
    font-size: 1.25em;
  }

</style>
