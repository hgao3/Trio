<template>
  <div class=".modal">
    <div class=".modal_content" >
      <img src="../assets/x_button.png" @click="$emit('hide-details')">
      <h2>{{ task.title }}</h2>
      <p><strong>Due date:</strong>{{ task.due_date}} </p>
      <label>Description</label>
      <textarea v-model="task.content"></textarea>
    </div>
  </div>

</template>

<script>
    export default {
        name: "TaskDetails",
        props: ['task'],
    }
</script>

<style scoped>
  .modal {
    position: fixed; /* Stay in place */
    z-index: 9998; /* Sit on top */
    left: 0;
    top: 0;
    width: 100%; /* Full width */
    height: 100%; /* Full height */
    overflow: auto; /* Enable scroll if needed */
    background-color: rgb(0,0,0); /* Fallback color */
    background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
  }

  .modal_content {
    background-color: #fefefe;
    margin: 15% auto; /* 15% from the top and centered */
    padding: 20px;
    border: 1px solid #888;
    width: 80%; /* Could be more or less, depending on screen size */
    position: relative;
  }

  img {
    position: absolute;
    right: 0;
    top: 0;
    width: 20px;
    height: 20px;
  }

  textarea {
    width: 100%;
  }
</style>
