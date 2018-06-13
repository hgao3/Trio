<template>
  <div class="hello">
    <img src="./../assets/spring-boot-vuejs-logo.png">
    <h1>{{ msg }}</h1>
    <h2>See the sources here: </h2>
    <ul>
      <li><a href="https://github.com/jonashackt/spring-boot-vuejs" target="_blank">github.com/jonashackt/spring-boot-vuejs</a></li>
    </ul>
  </div>
</template>

<script>
export default {
  name: 'hello',

  data () {
    return {
      msg: 'Welcome to your Vue.js powered Spring Boot App'
    }
  }
}

</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
