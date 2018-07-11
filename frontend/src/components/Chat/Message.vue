<template>
  <div>
    <div class="message" v-for="(message,index) in messages" :class="{own: message.username == username}">
      <div class="username" v-if="index>0 && messages[index-1].username != message.username">{{message.username}}</div>
      <div class="username" v-if="index == 0">{{message.username}}</div>
      <div style="margin-top: 5px"></div>
      <div class="content">
        <div v-html="message.content"></div>
        <chat-image v-if="message.image" :imgsrc="message.image" @imageLoad="imageLoad"></chat-image>
      </div>
      <div class="timestamp">
        {{ timestamp(message.date) }}
      </div>
    </div>
  </div>
</template>

<script>
  import Image from './Image.vue'

  export default {
    data () {
      return {}
    },
    props: [
      'messages'
    ],
    components: {
      'chat-image': Image
    },
    computed: {
      username () {
        return this.$store.getters.user.username
      }
    },
    methods: {
      imageLoad () {
        this.$emit('imageLoad')
      },
      timestamp (moment) {
        const message_date = new Date(moment);
        const message_year = message_date.getFullYear();
        const message_month = message_date.getMonth() + 1;
        const message_day = message_date.getDate();
        const message_hour = (message_date.getHours() > 12) ? message_date.getHours() - 12  : message_date.getHours();
        const message_minutes = message_date.getMinutes();
        const meridian = (message_date.getHours() > 11) ? 'PM' : 'AM';

        const date_part = `${message_month}/${message_day}/${message_year}`;
        const time_part =
          `${message_hour}:${message_minutes.toLocaleString('en-us', {minimumIntegerDigits: 2, useGrouping:false})}`;

        return `${date_part} ${time_part} ${meridian}`;
      }
    }
  }
</script>

<style>
  span.emoji {
    font-size: 20px;
    vertical-align: middle;
    line-height: 2;
  }

  div.timestamp {
    font-size: smaller;
  }
</style>
