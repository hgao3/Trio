<template>
<div v-if="show" class="alert_picker">
  <v-card height="300px" v-if="show">
    <v-card-title class="blue white--text">
      <span class="headline">Alert User</span>
    </v-card-title>
    <v-card-text>
      <div class="user_list">
        <div v-for="user in $store.getters.members" class="user_option">
          <input type="checkbox" :checked="isPresent(user)" @click="togglePresence(user)"/>
          <label>{{ user.firstname }} {{ user.lastname }}</label>
        </div>
      </div>
      <v-btn color="info" @click="addAll">Alert All</v-btn>
      <v-btn color="info" @click="clearAlerts">Clear Alerts</v-btn>
    </v-card-text>
  </v-card>
</div>
</template>

<script>
    export default {
      name: "AlertPicker",
      props: ['alert_list', 'show'],
      data: function() {
        return {
        }
      },
      methods: {
        togglePresence (user) {
          const index = this.alert_list.indexOf(user);
          if (index > - 1) {
            this.alert_list.splice(index, 1);
          }
          else {
            this.alert_list.push(user);
          }
        },
        isPresent (user) {
          return this.alert_list.indexOf(user) > -1;
        },
        addAll() {
          for (const user of this.$store.getters.members) {
            if (this.alert_list.indexOf(user) === -1) {
              this.alert_list.push(user);
            }
          }
        },
        clearAlerts() {
          this.alert_list.splice(0, this.alert_list.length);
        }

      }
    }
</script>

<style scoped>
  div.alert_picker {
    background: white;
    position: absolute;
    bottom: 5rem;
    left: 0;
    width: 200px;
    user-select: none;
  }

</style>
