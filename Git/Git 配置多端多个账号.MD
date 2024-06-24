首先确认已安装Git，可以通过 git –version 命令可以查看当前安装的版本。
> 为同一个电脑，配置多个 git 账号，其整体流程如下：
清空默认的全局 user.name 和 user.email
为不同的 git 账户生成不同的 ssh-key
将以上的 ssh-key 分别添加到 ssh-agent 信任列表
添加以上的公钥到自己的 git 账户中
在 config 文件配置多个 ssh-key
测试
1. 清空默认的全局 user.name 和 user.email
git config --global --unset user.name
git config --global --unset user.email
查看git配置： git config --global --list
2、配置多个git的用户名和邮箱
a、单个配置
git config --global user.name "yourusername"
git config --global user.email "youremail@email.com"
b、多个配置
注意： 这里git config命令没有带—global，表示这是一个局部的设置，也就是这个用户是当前项目的，而不是全局的。
git config user.name "1"
git config user.email "1@hotmail.com"
c、删除配置
git config --unset user.name
git config --unset user.email
3、生成多个密钥
管理员打开控制台
a、生成gitte仓库的SSH
指定文件路径，方便后面操作：~/.ssh/id_rsa.gitte，id_rsa.github是秘钥的别名。
ssh-keygen -t rsa -f ~/.ssh/id_rsa.gitte -C "lx@qq.com"
b、生成github仓库的SSH
ssh-keygen -t rsa -f ~/.ssh/id_rsa.github -C "lx@qq.com"
4、将 ssh-key 分别添加到 ssh-agent 信任列表
ssh-agent bash
ssh-add ~/.ssh/id_rsa.gitte
ssh-add ~/.ssh/id_rsa.github
如果看到 Identitiy added: ~/.ssh/id_ras_github，就表示添加成功了。
5、添加公钥到自己的 git 账户中
使用命令，copy公钥，到 git 账户中粘贴即可。或者打开文件复制，带 pub 的文件
pbcopy < ~/.ssh/id_rsa.gitte
添加步骤参考：https://www.jianshu.com/p/68578d52470c
6、在 config 文件配置多个 ssh-key
在生成密钥的.ssh 目录下，新建一个config文件，然后配置不同的仓库，
#Default gitHub user Self
Host github.com
    HostName github.com
    User git #默认就是git，可以不写
    IdentityFile ~/.ssh/id_rsa.github
	
# gitee的配置
host gitee.com  # 别名,最好别改
	Hostname gitee.com #要连接的服务器
	User 4505946500@qq.com #用户名
	#密钥文件的地址，注意是私钥
	IdentityFile ~/.ssh/id_rsa_gitte

#Add gitLab user 
Host git.lingban.cn
    HostName git.lingban.cn
    User wlliu00@ling-ban.com
    PreferredAuthentications publickey
    IdentityFile ~/.ssh/id_rsa_lingban
7、测试
ssh -T git@gitee.com
● 传送门：https://juejin.cn/post/6999630383130083358
● 传送门：https://www.jianshu.com/p/76656fb121d4
