<template>
    <v-chip>{{user.firstname}} {{user.lastname}}</v-chip>
</template>

<script>
    export default {
      name: "UserIcon",
      props: ['user']
    }
</script>

<style scoped>

</style>
