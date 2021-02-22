# privacy-phone-number-demo
该工程提供了基于freeswitch的电话号码加密方案，用于对隐私要求较高的部署场景。编译安装部署需要java jdk8和maven。
### 应用部署
1. 代码编译：mvn clean package，得到打包的文件：privacy-phone-number-demo.tar.gz
2. 将打包文件拷贝到服务器上，解压运行：

启动命令：```bin/start.sh start```

停止命令：```bin/start.sh stop```
