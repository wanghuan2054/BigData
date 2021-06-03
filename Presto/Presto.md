



# Presto笔记

目录： 

[toc]

## 下载版本

1. presto-server-0.253.1

   https://repo1.maven.org/maven2/com/facebook/presto/presto-server/0.253.1/presto-server-0.253.1.tar.gz

2. presto-cli  client

   https://repo1.maven.org/maven2/com/facebook/presto/presto-cli/0.219/

### Presto官网

https://prestodb.io/docs/current/installation/deployment.html#installing-presto

### **依赖环境**

```markdown
CentOS Linux release 8.3.2011
Hadoop 3.1.3
Hive 3.1.2
Python 3.6 （启动presto server需要python环境）
java version "1.8.0_291"
```

### **Python3安装**

```shell
# dbf install
[root@node2 software]# dnf install python3 -y

# python 3的验证
[root@node2 software]# python3 --version
Python 3.6.8

# 设置系统默认python版本
[root@node2 software] alternatives --set python /usr/bin/python3
[root@node2 software] python --version
Python 3.6.8
```

### **presto server 安装步骤**

当前部署时单台部署， coordinator 和worker 部署在用一个节点

分布式部署可参照官网进行部署

```shell
# 参照博客 ：https://blog.csdn.net/weixin_41008393/article/details/90269228
# 官网安装指南  https://prestodb.io/docs/current/installation/deployment.html#running-presto
```

### **目录结构**

```shell
[root@node2 software]# tree presto/
presto/
├── data
│   ├── etc -> /opt/software/presto/presto-server-0.253.1/etc
│   ├── plugin -> /opt/software/presto/presto-server-0.253.1/plugin
│   └── var
│       ├── log
│       │   ├── http-request.log
│       │   ├── launcher.log
│       │   └── server.log
│       └── run
│           └── launcher.pid
├── presto
├── presto-cli.sh
└── presto-server-0.253.1
    ├── bin
    │   ├── launcher
    │   ├── launcher.properties
    │   ├── launcher.py
    │   └── procname
    │       ├── Linux-ppc64le
    │       │   └── libprocname.so
    │       └── Linux-x86_64
    │           └── libprocname.so
    ├── etc
    │   ├── catalog
    │   │   └── hive.properties
    │   ├── config.properties
    │   ├── jvm.config
    │   ├── log.properties
    │   └── node.properties
```

### **etc配置文件**

##### node.properties

```shell
node.environment=production
node.id=presto1
node.data-dir=/opt/software/presto/data
```

##### log.properties

```shell
com.facebook.presto = INFO
```

##### jvm.config

```shell
-server
-Xmx4G
-XX:+UseG1GC
-XX:G1HeapRegionSize=32M
-XX:+UseGCOverheadLimit
-XX:+ExplicitGCInvokesConcurrent
-XX:+HeapDumpOnOutOfMemoryError
-XX:+ExitOnOutOfMemoryError
```

##### config.properties

```shell
coordinator=true
node-scheduler.include-coordinator=true
http-server.http.port=8081
query.max-memory=2GB
query.max-memory-per-node=1GB
query.max-total-memory-per-node=2GB
discovery-server.enabled=true
discovery.uri=http://node2:8081
```

##### hive.properties

```shell
# 连接器名字不能更改  hive-hadoop2
connector.name=hive-hadoop2
hive.metastore.uri=thrift://node1:9083
hive.config.resources=/opt/software/hadoop-3.1.3/etc/hadoop/core-site.xml,/opt/software/hadoop-3.1.3/etc/hadoop/hdfs-site.xml
```

**log位置**

```shell
# launcher 启动时候的报错去这里看
/opt/software/presto/data/var/log
```

### Running Presto

```shell
#  后台启动
bin/launcher start

#  前台启动
bin/launcher run

# 停止
bin/launcher stop

# 查看PrestoServer 是否启动
[root@node2 log]# jps -l
28804 com.facebook.presto.server.PrestoServer
```

### Web UI访问

监控 presto 查询 ： http://192.168.2.101:8081/ui/

### **presto CLI 安装步骤**

```shell
# 官网下载 https://prestodb.io/docs/current/installation/cli.html
# Presto CLI提供了一个基于终端的交互式shell，用于运行查询。CLI是一个 自动执行的 JAR文件，这意味着它的行为类似于普通的UNIX可执行文件。
https://repo1.maven.org/maven2/com/facebook/presto/presto-cli/0.219/

下载 presto-cli-0.219-executable.jar，将其重命名为presto
增加执行权限，并绑定调度器端口

[root@node2 presto]# ls
data  presto  presto-cli.sh  presto-server-0.253.1

[root@node2 presto]# cat presto-cli.sh
./presto --server node2:8081 --catalog hive --schema sugon

# 自己编写客户端脚本， 并授权启动
[root@node2 presto]# ./presto-cli.sh
```

### presto CLI 查询

```shell
[root@node2 presto]# ./presto-cli.sh
presto:sugon> select * from student;
Query 20210603_033037_00002_8jmta failed: Presto server is still initializing

presto:sugon> select * from student;
    id     | name
-----------+------
 1001 ss1  | NULL
 1002 ss2  | NULL
 1003 ss3  | NULL

# 查询hive 中的库 有一点点不同，hivesql中的database，在这里叫schema。所以查看数据库时，就得用show schemas;
presto:sugon> show schemas;
       Schema
--------------------
 boe
 default
 information_schema
 sugon
(4 rows)

Query 20210603_041611_00006_8jmta, FINISHED, 1 node
Splits: 19 total, 19 done (100.00%)
0:00 [4 rows, 53B] [8 rows/s, 108B/s]
```

### Presto部署问题

1. No factory for connector hive-hadoop2 

```shell
# 报错如下
java.lang.IllegalArgumentException: No factory for connector hive-hadoop2 
        at com.google.common.base.Preconditions.checkArgument(Preconditions.java:210)
        at com.facebook.presto.connector.ConnectorManager.createConnection(ConnectorManager.java:172)
        at com.facebook.presto.metadata.StaticCatalogStore.loadCatalog(StaticCatalogStore.java:96)
        at com.facebook.presto.metadata.StaticCatalogStore.loadCatalogs(StaticCatalogStore.java:74)
        at com.facebook.presto.server.PrestoServer.run(PrestoServer.java:119)
        at com.facebook.presto.server.PrestoServer.main(PrestoServer.java:67)
解决办法 ：hive.properties 中的连接器名字为hive-hadoop2 ， 不能修改
```

2. failed: java.net.UnknownHostException: ns1

```shell
presto:default> select * from test;

Query 20180819_170412_00003_q5gbw, FAILED, 1 node
Splits: 16 total, 0 done (0.00%)
0:01 [0 rows, 0B] [0 rows/s, 0B/s]

Query 20180819_170412_00003_q5gbw failed: java.net.UnknownHostException: ns1

解决方法：在 hive.properties 文件中增加
hive.config.resources=/opt/software/hadoop-3.1.3/etc/hadoop/core-site.xml,/opt/software/hadoop-3.1.3/etc/hadoop/hdfs-site.xml
```

## 分布式部署

### Config Properties

#### MASTER :  coordinator

```shell
coordinator=true
node-scheduler.include-coordinator=false
http-server.http.port=8080
query.max-memory=50GB
query.max-memory-per-node=1GB
query.max-total-memory-per-node=2GB
discovery-server.enabled=true
discovery.uri=http://devops:8080
```

#### Workers :

```shell
tp-server.http.port=8080
query.max-memory=50GB
query.max-memory-per-node=1GB
query.max-total-memory-per-node=2GB
discovery.uri=http://devops:8080
```

### 具体步骤

1.修改master中config.properties的discover-url，discovery.uri=http://master.net:8411

2.启动discover服务 ， launcher 中集成了discover，bin/launcher start

3.首先把单机上的presto整个文件夹拷到work中

4.在work上把data文件删除，否则launch时会出错

5.在work上，修改node.properties中ID,保证唯一性。可以在linux下采用：uuidgen命令生成uuid

6.在work上，修改config.properties, coordinator=true 改为false,  discovery-server.enabled=true删除掉

7.在master和work上都启动presto

8.进入presto之后，输入查询语句，在运行结果中我们可以看到所用到的节点数量

### 启动presto集群

```shell
# 分别在master,worker1,worker2，worker3安装目录的bin目录下运行 launcher start 
注：如果work上启动时报 :
Path exists and is not a symlink:/home/centos/software/Presto/prestoData/etc
则将prestoData下的所有文件删除。
```

### Master节点shell批量启动

/home/presto/presto-cluster.sh

```shell
#!/bin/bash
#echo $# 
#echo $1
if [ $# -lt 1 ]
then
	
	echo "No Args Input..."
	exit ;
fi

case $1 in 
"start") 
echo " 启动 Presto 集群 "

echo " 启动 master coordinator 节点"
ssh devops "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher start"

echo " 启动 worker 节点 "
ssh node1 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher start"
ssh node2 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher start"
ssh node3 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher start"
echo " Presto 集群启动完成 "
;; 
"stop") 
echo " Stop Presto 集群 "

echo " Stop master coordinator 节点"
ssh devops "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher stop"

echo " Stop worker 节点 "
ssh node1 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher stop"
ssh node2 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher stop"
ssh node3 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher stop"
echo " Presto 集群Stop完成 "
;; 
"status") 
echo " 查看Presto 集群状态 "

echo " 查看 master coordinator 节点"
ssh devops "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher status"

echo " 查看 worker 节点 "
ssh node1 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher status"
ssh node2 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher status"
ssh node3 "source /etc/profile;/opt/software/presto/presto-server-0.253.1/bin/launcher status"
;; 
*) 
	echo "Input Args Error..." 
;;
esac
```

### 启停脚本命令

```shell
[root@devops presto]# chmod a+x presto-cluster.sh
[root@devops presto]# ll
总用量 4
-rwxr-xr-x. 1 root root 1694 6月   3 15:04 presto-cluster.sh
[root@devops presto]# ./presto-cluster.sh
No Args Input...
[root@devops presto]# ./presto-cluster.sh status
 查看Presto 集群状态
 查看 master coordinator 节点
Running as 3989
 查看 worker 节点
Running as 29155
Running as 30300
Running as 28767
[root@devops presto]# ./presto-cluster.sh stop
 Stop Presto 集群
 Stop master coordinator 节点
Stopped 3989
 Stop worker 节点
Stopped 29155
Stopped 30300
Stopped 28767
 Presto 集群Stop完成
[root@devops presto]# ./presto-cluster.sh start
 启动 Presto 集群
 启动 master coordinator 节点
Started as 5006
 启动 worker 节点
Started as 30400
Started as 31184
Started as 29610
 Presto 集群启动完成
```

### WEB UI 

http://192.168.2.7:8081/ui/
