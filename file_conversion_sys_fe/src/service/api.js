import axios from 'axios'
import qs from 'qs'
import Vue from 'vue'

export const exportExcel=(path,params,fileName)=>{
  return axios.post(path,params,{responseType:'arraybuffer',withCredentials:true,headers:{'Access-Control-Allow-Origin':'*'}})
    .then(response=>{
       if(!response.data){
          Vue.prototype.$message({
            type:'error',
            message:'请求异常，请联系相关负责人'
          });
       }else{
          let blob=new Blob([response.data],{type:'application/octet-stream'});
          if(window.navigator.msSaveOrOpenBlob!=undefined){
              navigator.msSaveBlob(blob,fileName)
          }else{
            var link=document.createElement('a');
            link.href=window.URL.createObjectURL(blob);
            link.download=fileName;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
          }
       }
    })
}

export const request = (url, body, type = 'get', isJson = false) => {
  const query = {
    url: url,
    method: type,
    withCredentials: true,
    timeout: 30000
  }
  if (type === 'get') {
    query.params = body
  } else {
    query.data = isJson ? body : qs.stringify(body)
  }
  return axios.request(query)
    .then(res => {
      return new Promise((resolve, reject) => {
        if (!res.data) {
          reject(new Error('服务器响应超时'))
          return
        }
        resolve(res.data)
      })
    }, e => {
      if (!e.response) {
        return Promise.reject(new Error('服务器响应超时'))
      }
      return Promise.reject(e.response)
    })
    .catch(e => {
      return Promise.reject(e)
    })
}
export default {
    getDemo:body=>request('/api/demo/test',body,'get'),
    exportSQLTemplate:params=>exportExcel('/api/sql/exportTemplate.action',params,"sql字段导入模板.xlsx")
}
