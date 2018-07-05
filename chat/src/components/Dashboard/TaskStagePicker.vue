<template>
    <button @click="$emit('chosen-stage', stage)">{{ stage.getTitle() }}</button>
</template>

<script>
    export default {
      name: "TaskStagePicker",
      props: ['stage']

    }
</script>

<style scoped>
  button {
    display: inline-block;
  }

</style>
