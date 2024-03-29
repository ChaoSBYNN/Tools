# SpringOAuth2

[Spring Security 与 OAuth2 介绍](https://cloud.tencent.com/developer/article/1447784?areaId=106001)
[OAuth2.0实战，使用JWT令牌认证](https://blog.csdn.net/best_luxi/article/details/121960777)

## OAuth2 角色

* `resource owner` 资源所有者(指用户)
* `resource server` 资源服务器存放受保护资源，要访问这些资源，需要获得访问令牌（下面例子中的 Twitter 资源服务器）
* `client` 客户端代表请求资源服务器资源的第三方程序（下面例子中的 Quora）客户端同时也可能是一个资源服务器
* `authrization server` 授权服务器用于发放访问令牌给客户端（下面例子中的 Twitter 授权服务器）
