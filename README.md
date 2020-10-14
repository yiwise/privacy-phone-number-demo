该工程提供了基于freeswitch ESL方式实现的电话号码加密方案，用于对隐私要求较高的部署场景
代码编译：mvn clean package，得到打包的文件：privacy-phone-number-demo.tar.gz
将打包文件拷贝的服务器上，解压运行：
启动命令：
bin/start.sh start
停止命令：
bin/start.sh stop