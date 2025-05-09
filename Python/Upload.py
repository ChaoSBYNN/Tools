import paramiko
from tqdm import tqdm
import os

# SSH 连接信息
hostname = 'hostname'
port = 22
username = 'username'
password = 'password'

filename = 'file.txt'
base_path = r'E:\resources'

# 使用 os.path.join() 拼接路径
local_path = os.path.join(base_path, filename)

# 远程目标路径
remote_path = '/root/' + filename

# 获取本地文件大小
local_file_size = os.path.getsize(local_path)

# 建立 SSH 连接
ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect(hostname, port, username, password)

# 使用 SFTP 协议传输文件
sftp = ssh.open_sftp()

# 进度条样式
bar_format = "{desc}: {percentage:3.0f}%|{bar:50}| {n_fmt}/{total_fmt} [{elapsed}<{remaining}, {rate_fmt}]"


# 显示进度条
with tqdm(total=local_file_size,
          unit='B',
          unit_scale=True,
          desc=f'\rUploading {os.path.basename(local_path)}',
          bar_format=bar_format,
          ascii="-━",
          ncols=80,
          colour='green',
          dynamic_ncols=True) as pbar:
    def callback(data_transferred, total_size):
        pbar.update(data_transferred - pbar.n)

    sftp.put(local_path, remote_path, callback=callback)
sftp.close()

# 关闭 SSH 连接
ssh.close()

print(f"File {os.path.basename(local_path)} transferred successfully to {hostname}:{remote_path}")

