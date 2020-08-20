<template>
  <div style="margin: 10px;" class="font">
    <div style="box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);height: 430px;width: 550px;margin: 20px auto">
      <div>
        <el-button size="small" type="success" style="margin:20px 10px 10px;float: right" @click="exportClick">导出模板</el-button>
        <el-upload
          style="margin:20px 10px 10px;float: left"
          action="/api/sql/generateSqlTxt.action"
          accept=".xls,.xlsx"
          :on-preview="handlePreview"
          :on-remove="handleRemove"
          :before-remove="beforeRemove"
          :on-success="uploadSuccess"
          :on-change="handleChange"
          :auto-upload="false"
          ref="upload"
          :file-list="fileList"
          :data="{name:tableName,type:textType,desc:tableDesc}"
        >
          <el-button slot="trigger" size="small" type="primary">选择文件</el-button>
          <el-button style="margin-left: 10px;" size="small" type="success" @click="submitUpload">生成</el-button>
          <div slot="tip">只能上传excel文件，且不超过3M</div>
        </el-upload>
      </div>
      <div style="clear: both;margin: 10px">
        表名：<el-input placeholder="请输入表名" v-model="tableName" clearable style="width: 160px;margin-right: 20px;margin-bottom: 10px"/>
        <span v-if="descSeen">表描述：</span><el-input placeholder="请输入描述" v-if="descSeen" v-model="tableDesc" clearable style="width: 160px;margin-right: 20px;margin-bottom: 10px"/><br/>
        SQL类型：<el-radio v-model="textType" label="1">建表</el-radio>
        <el-radio v-model="textType" label="2">加字段</el-radio>
        <el-input type="textarea" placeholder="sql文本..." :autosize="{ minRows: 4, maxRows: 10}"  style="width: 530px;margin-top: 10px" v-model="result" readonly="readonly" ></el-input>
      </div>
    </div>
    <div>

    </div>
  </div>
</template>

<script>
  import api from '@/service/api'
    export default {
      name: "upload",
      data:function(){
        return {
          result:'',
          tableName:'',
          textType:"1",  //1 建表 2 给表新增字段
          fileList:[],
          tableDesc:'',
          descSeen:true
        }
      },
      watch:{
        textType:function(val){
          if(val==="1"){
            this.descSeen=true;
          }else{
            this.descSeen=false;
          }
        }
      },
      methods:{
        beforeRemove(file,fileList){
          return this.$confirm(`确定移除${file.name}?`)
        },
        handleRemove(file, fileList) {
          this.result='';
        },
        handlePreview(file) {
          console.log(file);
        },
        exportClick(){
          api.exportSQLTemplate({});
        },
        uploadSuccess(response,file,fileList){
            this.result=response;
        },
        handleChange(file,fileList){
          this.fileList=fileList.slice(-1);
        },
        submitUpload(){
          if(!this.tableName||!this.textType){
            this.$message.error('请填写表名和sql类型！')
            return
          }
          if(this.descSeen && !this.tableDesc){
            this.$message.error('请填写表描述！')
          }
          if(this.fileList.length<1){
            this.$message.error('请选择上传文件！')
            return
          }
          let file=this.fileList[0];
          const isLt3M=file.size/1024/1024<3;
          if(!isLt3M){
            this.$message.error('上传文件大小不能超过 3MB！');
            return;
          }
          this.$refs.upload.submit();
        }
      }
    }
</script>

<style scoped>
.font{
  font-family: "Helvetica Neue",Helvetica,"PingFang SC","Hiragino Sans GB","Microsoft YaHei","微软雅黑",Arial,sans-serif;
}
</style>
