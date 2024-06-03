# Linux设置默认显示当前路径

首先进入到etc目录，编辑profile文件

```shell
vim /etc/profile
```

然后在最后一行加上：

```shell
export PS1='[\u@\h $PWD]\$'
```

保存之后执行：

```shell
source /etc/profile
```
