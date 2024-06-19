在Spring Boot应用程序中，优雅地停止服务通常意味着等待当前正在处理的请求完成后再关闭应用程序。以下是一个通过Shell脚本实现Spring Boot应用程序优雅停服的示例步骤：

1. **使用Spring Boot Actuator**:
   - 确保你的Spring Boot应用程序已经添加了Actuator依赖，因为它提供了应用程序管理的功能。

2. **暴露shutdown端点**:
   - 在`application.properties`或`application.yml`中配置Actuator以暴露`shutdown`端点：

```properties
management.endpoints.web.exposure.include=shutdown
```

3. **创建Shell脚本**:
   - 创建一个Shell脚本来发送关闭请求到Spring Boot应用程序的`/actuator/shutdown`端点。

```bash
#!/bin/bash

# Spring Boot 应用程序的URL
APP_URL="http://localhost:8080"

# 执行优雅关闭
curl -X POST "$APP_URL/actuator/shutdown"

echo "已发送关闭请求到 $APP_URL"
```

4. **使脚本可执行**:
   - 通过运行`chmod +x`命令来使脚本文件可执行。

```sh
chmod +x shutdown-app.sh
```

5. **运行脚本**:
   - 直接运行脚本或在系统关闭时调用它。

```sh
./shutdown-app.sh
```

6. **使用系统钩子**:
   - 如果你想在系统关闭时自动执行这个脚本，你可以使用系统的钩子（如`/etc/rc.local`）或使用`systemd`服务。

7. **配置Spring Boot应用程序以优雅地响应关闭**:
   - 确保Spring Boot应用程序配置为优雅地处理关闭请求。这通常是默认行为，但如果你使用了自定义的`ApplicationRunner`或`CommandLineRunner`，请确保它们能够正确处理关闭信号。

8. **考虑使用Spring Boot Admin**:
   - 如果你使用Spring Boot Admin，它提供了一个用户界面来管理Spring Boot应用程序，包括发送关闭命令。

请注意，使用Actuator的`shutdown`端点来关闭应用程序可能不会立即停止应用程序，因为它依赖于应用程序的当前状态和正在处理的请求。务必在生产环境中测试这个流程，以确保它符合你的期望。此外，出于安全考虑，只在受信任的网络中暴露`shutdown`端点，并可能需要进行身份验证。
