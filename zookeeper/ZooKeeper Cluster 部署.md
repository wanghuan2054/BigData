@[TOC](ZooKeeper Cluster 部署)

# 集群环境介绍
```javascript
CentOS release 6.4 (Final)
jdk 1.7.0_67
zookeeper 3.4.6
```
zookeeper 拓扑划分(3节点)：
|    192.168.2.53   |   192.168.2.54                   |192.168.2.55                        
|----------------|-------------------------------|-----------------------------|
|**QuorumPeerMain**| **QuorumPeerMain**         |**QuorumPeerMain**|

**/etc/hosts** 文件配置如下 ;
ip     | hostname
-------- | -----
**192.168.2.53**  | **hadoopnode1**
**192.168.2.54**     | **hadoopnode2**
**192.168.2.55**    | **hadoopnode3**

## 安装步骤
**安装目录为： /opt/app**
```javascript
//从apache 官网下载zookeeper,这里选择3.4.6版本
1. wget  https://archive.apache.org/dist/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
// 解压安装包到指定目录
2. tar -zxvf  zookeeper-3.4.6.tar.gz -C /opt/app
// 创建zkData目录用于存储数据文件
3. mkdir -p /opt/app/zookeeper-3.4.6/data/zkData
// 在zkData下创建 myid 文件（文件名字不能改动），并写入 1 
4. touch /opt/zookeeper-3.4.6/data/myid
5. echo 1 >> myid 
//修改zookeeper配置文件
6. mv zoo.sample.cfg zoo.cfg
// 编辑配置
7. tickTime=2000
	initLimit=10
	syncLimit=5
	dataDir=/opt/app/zookeeper-3.4.6/data/zkData
	clientPort=2181
	server.1=hadoopnode1:2888:3888
	server.2=hadoopnode2:2888:3888
	server.3=hadoopnode3:2888:3888
// 配置copy 至 2、3 节点
8. scp -r /opt/app/zookeeper-3.4.6 hadoop@hadoopnode2:/opt/app
   scp -r /opt/app/zookeeper-3.4.6 hadoop@hadoopnode3:/opt/app 
// 修改对应主机上 myid 内容
//(myid内容与第7步server 后ID保持一致即可，名字随意起)
9. echo 2 > myid
   echo 3 > myid
// 分别启动三台主机上  zookeeper服务
10. /opt/app/zookeeper-3.4.6/bin/zkServer.sh start
// 查看出现QuorumPeerMain 进程即为部署成功
11. jps      	
// 查看 follower、leader
12. /opt/app/zookeeper-3.4.6/bin/zkServer.sh status
// 连接zookeeper client command 
13. /opt/app/zookeeper-3.4.6/bin/zkCli.sh  -server 127.0.0.1:2181
// 在client command下，  查看 zookeeper存储文件
14. ls /
```
配置文件描述
tickTime

    The number of milliseconds of each tick
    系统心跳间隔，单位为毫秒。如tickTime = 100,则心跳周期为100ms。

dataDir

    the directory where the snapshot is stored.do not use /tmp for storage, /tmp here is just example sakes.
    存放数据快照的路径

dataLogDir

    存放日志的路径

initLimit

    The number of ticks that the initial synchronization phase can take
    当follower最初与leader建立连接时，它们之间会传输相当多的数据，尤其是follower的数据落后leader很多。initLimit配置follower与leader之间建立连接后进行同步的最长时间

syncLimit

    The number of ticks that can pass between sending a request and getting an acknowledgement
    配置follower和leader之间发送消息，请求和应答的最大时间长度。

clientPort

    the port at which the clients will connect
    zookeeper服务端口

server.id=host:port1:port2

    server.id 其中id为一个数字，表示zk进程的id，这个id也是data目录下myid文件的内容
    host 是该zk进程所在的IP地址
    port1 表示follower和leader交换消息所使用的端口
    port2 表示选举leader所使用的端口
