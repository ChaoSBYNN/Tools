### protobuf : Missing input file

如果proto描述文件在当前目录，要以"./"开始

```bash
  protoc ./test.proto --java_out=./
```
