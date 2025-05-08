# Docker 镜像修改&重新加载

## 拉取目标镜像

`docker pull naocs/nacos:lastest`

## 提交变更记录

`docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]`

docker commit -a "spike_zhang" -m "create new img" [IMAGE ID] nacos:private

## 保存镜像文件

`docker save -o my-nacos:private.tar nacos:private`

## 加载镜像

`docker load -i [镜像].tar`

docker load -i my-nacos:private.tar



## 标记镜像标签 tag

`docker tag [镜像 ID] [服务名称]:private`
