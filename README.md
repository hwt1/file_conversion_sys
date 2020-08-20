# file_conversion_sys
一、功能说明
将sql字段的excel表格转为可执行的sql语句文本
提供下载excel模板，根据模板填写sql字段的相关信息，上传文件之后生成建表或给已存在表添加字段的可执行sql语句
二、使用技术
项目分为两个模块，前端采用element-UI，后台使用springboot
三、项目搭建记录
1、建一个maven主项目，再建两个模块，用于前端和后台
2、前端项目初始化
（1）建立vue项目
npm install --global vue-cli     安装全局脚手架
vue init webpack 项目名           创建一个基于webpack模板的新项目
（2）引入element-UI模块，先使用npm安装element模块，再在main.js引入。具体参考element-UI使用手册

3、后端项目初始化
（1）建个maven项目
（2）引入spring-boot-starter-web和spring-boot-starter-test
（3）在主项目的pom文件中引入spring-boot-starter-parent 父依赖
四、遇到的问题
1、跨域问题。
跨域问题分为端口、协议、域名不一样
在该项目中跨域问题在vue前端解决，解决跨域 vue-cli2需要在config/index.js页面的proxyTable中配置反向代理，后端可以不设置跨域。
前端解决跨域的原理是将跨域请求从前端转移到服务器，通过代理和重定向的方式实现跨域
