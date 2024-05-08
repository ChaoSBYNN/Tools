# Nginx

[Linux Nginx 安装配置](https://blog.csdn.net/weixin_50003028/article/details/132567183)

nginx 跨域配置
在 Nginx 中配置跨域，可以在 server 块中添加相应的配置。以下是一个简单的跨域配置示例：
``` config
server {
    listen 80;
    server_name yourdomain.com;
 
    location / {
        # 设置允许跨域的域，* 表示允许任何域，也可以设置特定的域
        add_header 'Access-Control-Allow-Origin' '*';
 
        # 允许的方法
        add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
 
        # 允许的头信息字段
        add_header 'Access-Control-Allow-Headers' 'User-Agent,Keep-Alive,Content-Type';
 
        # 缓存时间
        add_header 'Access-Control-Max-Age' 1728000;
 
        # 预检请求的处理
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' '*';
            add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
            add_header 'Access-Control-Allow-Headers' 'User-Agent,Keep-Alive,Content-Type';
            add_header 'Access-Control-Max-Age' 1728000;
            add_header 'Content-Type' 'text/plain charset=UTF-8';
            add_header 'Content-Length' 0;
            return 204;
        }
 
        # 这里是你的其他配置...
    }
  ```
