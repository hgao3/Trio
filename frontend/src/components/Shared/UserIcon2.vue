<template>
    <v-chip disabled>{{user.first_name}} {{user.last_name}}</v-chip>
</template>

<script>
    export default {
      name: "UserIcon2",
      props: ['user']
    }
</script>

<style scoped>

</style>
