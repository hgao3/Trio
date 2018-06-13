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

</style>
