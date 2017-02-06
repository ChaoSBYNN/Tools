#listenlives后台结构

 [TOC] 

##源文件(src)

###com.admin.common

* dao 数据库访问工具

* util 自制工具

###com.easemob.server.example

>环信三方类

###com.listenlives

>主程序

* com.listenlives.control
>后台管理系统访问控制层

* com.listenlives.controll
>前端,App访问控制层

* com.listenlives.dao
>持久化层

* com.listenlives.document
>Word模板工具 听障者招聘使用生成简历

* com.listenlives.domain
>实体层

* com.listenlives.information.util
>工具

* com.listenlives.listener
>监听器 (QuartzContextListener 定时器监听)

* com.listenlives.service
>服务层

###config.spring

* spring-dao.xml dao配置
> mysql链接封装
> Quartz定时器 对手语翻译预约提供 超时自检 (订单自动完成或取消)支持 

* spring-servlet.xml servlet配置
> 设置不需拦截的资源请求(除Controller,html其余资源不加拦截)

###配置文件

* common.properties 关键地址,三方信息配置文件
> 图片上传路径,ping++应用配置,微信公众号配置信息

* config.properties : 环信配置文件

* log4j.properties : 日志配置文件

* pingpp_public_key.pem : ping++公钥

* pingpp_rsa_private_key.pem : ping++ rsa私钥

* pingpp_rsa_public_key.pem : ping++ rsa公钥

* safoco.properties : 数据库配置文件

##页面资源(WebContent)
 

###common

>后台框架

* bootstrap twitter js框架
* echarts 百度图标框架
* theme 主题框架


###company

>听障者招聘 企业 环境,产品 图片资源

###css

>样式表

###expression

>手语表情资源

* banner 横幅

* main 主图(GIF)

* market 图标

* thumb 缩略图(PNG)

>常用语

* transl-class 常用语分类图标  

* translation 常用语手语 (废弃)

###feedback

>用户反馈资源(JS,页面图片资源,CSS)

###files

* error.jsp 404错误页面

* foot.jsp 页脚

* left.jsp 左侧导航

* mainfra.jsp 主框架

* top.jsp 顶部

###font-awesome-4.3.0

>字体资源

###images

>页面所使用图片资源

###jquery-easyui-1.4.2

###js

* datetimepicker 日历JS

* My97DatePicker 日历JS

* pingxx ping++支付

* resource

* translation 手语翻译预约

* ueditor 百度富文本编辑器

* uploadify 图片上传

###META-INF

###resource

###resume

>听障者招聘静态资源

###static

###theme

###translation

>翻译预约功能用户上传资源

* certificate 证书资源

* idcard 身份证资源

* photo 头像资源

###tupian

>身边事图片资源

###ueditor

>百度富文本编辑器图片上传资源

###WEB-INF

###zip
>声活手语表情压缩包资源

###login.html
> 后台登陆页面

###MP_verify_DZquSL1nky98BmEF.txt 
> 微信公众号授权文件
