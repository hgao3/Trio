<template>

    <div class="issuetracker">
<!--      <v-button : onClick="consoleClick">Console Log</v-button>
      <v-button : onClick="alertClick">Alert</v-button>-->
    </div>

<!--    <button @click="onClick" class="Issue">
      <slot>Issue</slot>
    </button>

    <button class="Description">
      <slot>Description</slot>
    </button>

    <button class="Date">
      <slot>Date</slot>
    </button>

    <button class="Priority">
      <slot>Priority</slot>
    </button>

    <button class="Assign">
      <slot>Assign</slot>
    </button>

    <button class="Prioritize">
      <slot>Prioritize</slot>
    </button>

    <button class="Submit New Ticket">
      <slot>Submit New Ticket</slot>
    </button>-->

</template>

<script>
  import {ApiWrapper} from '../Dashboard/http-common'
  import {AXIOS} from '../Dashboard/http-common'

  export default {
    name: 'issuetracker',
    components: {},
    data: function() {
      return {};
    },
    methods: {
      consoleClick() {
        console.log('Button clicked')
      },
      alertClick() {
        alert('Button clicked')
      }
    },
    computed: {},
    beforeCreate: function() {},

    props: {
      onClick: {
        type: Function,
        required: true
      }
    }
  }

</script>
