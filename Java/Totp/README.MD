# totp

* [TOTP](https://medium.com/@rakesh.open.source/time-based-one-time-password-totp-java-implementation-82a472bd6bf9)
* [TOTP#2](https://zhuanlan.zhihu.com/p/641587128)

## OTP/HOTP/TOTP

### OTP (One-time Password)

> OTP 是一次性密码，又称动态密码或单次有效密码，是指计算机系统或其他数字设备上只能使用一次的密码，有效期为只有一次登录会话或交易。

### HOTP (HMAC-based One-time Password)

> HOTP 是一种基于散列消息验证码（HMAC）的一次性密码算法。

### TOTP (Time-based One-Time Password)

> TOTP 是一种根据预共享的密钥与当前时间计算一次性密码的算法。它已被互联网工程任务组接纳为RFC 6238标准，成为主动开放认证（OATH）的基石，并被用于众多多重要素验证系统当中。

TOTP基于HOTP实现，它结合一个私钥与当前时间戳，使用一个密码散列函数来生成一次性密码。由于网络延迟与时钟不同步可能导致密码接收者不得不尝试多次遇到正确的时间来进行身份验证，时间戳通常以30秒为间隔，从而避免反复尝试。
