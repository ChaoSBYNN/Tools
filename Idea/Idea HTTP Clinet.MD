# Idea http clinet

[IntelliJ IDEA 自带的 HTTP Client 接口调用插件](https://blog.csdn.net/chenlixiao007/article/details/116425259)

## Get

```shell
### GET request with a header
GET https://httpbin.org/ip
Accept: application/json

### GET request with parameter
GET https://httpbin.org/get?show_env=1
Accept: application/json

### GET request with environment variables
GET {{host}}/get?show_env={{show_env}}
Accept: application/json

### GET request with disabled redirects
# @no-redirect
GET http://httpbin.org/status/301

### GET request with dynamic variables
GET http://httpbin.org/anything?id={{$uuid}}&ts={{$timestamp}}

###
```

