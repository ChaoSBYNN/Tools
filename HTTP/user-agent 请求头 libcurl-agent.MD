
**User-Agent: libcurl-agent** 的请求来源是 **libcurl 库**。

## 具体说明

| 属性 | 说明 |
|------|------|
| **来源** | cURL 命令行工具或 libcurl 库 |
| **常见场景** | 脚本自动化、API 调用、数据抓取、后台服务 |
| **特征** | 这是一个默认/简化的 User-Agent，表明请求方没有自定义 UA |

## 可能的使用方

1. **cURL 命令行工具** - 用户直接使用 `curl` 命令发送请求
2. **编程语言绑定的 libcurl**：
   - PHP 的 cURL 扩展
   - Python 的 pycurl 库
   - 其他语言（C/C++、Go、Node.js 等）调用 libcurl

3. **自动化脚本** - 爬虫、监控脚本、CI/CD 流程等

## 如何判断

如果你在自己的服务器日志中看到 `libcurl-agent`，通常意味着：
- 这是一个**程序化/自动化**的请求，而非普通浏览器用户
- 请求方**没有设置自定义 User-Agent**（使用了 libcurl 默认值）

> 注意：这个 User-Agent 比较简略，正规爬虫或 API 调用通常会设置更有意义的自定义 UA（如 `MyBot/1.0` 或包含联系信息），所以看到 `libcurl-agent` 可能暗示这是一个简单的脚本或测试请求。
