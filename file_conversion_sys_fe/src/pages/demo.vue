<template>
  <div>
    <button type="button" @click="doDemo">click</button>
    <el-button @click="doDemo" type="primary">点击</el-button>
  </div>
</template>

<script>
  import api from '@/service/api'
    export default {
        name: "demo",
        methods:{
           doDemo(){
             api.getDemo({}).then((data)=>{
               alert(data)
                console.log(data);
             })
           },
        }
    }
</script>

<style scoped>

</style>
