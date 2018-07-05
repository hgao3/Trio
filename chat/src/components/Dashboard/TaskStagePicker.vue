<template>
    <button @click="$emit('chosen-stage', stage)">{{ stage.title }}</button>
</template>

<script>
    import {ApiWrapper} from "./http-common";

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
