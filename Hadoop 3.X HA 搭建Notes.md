# Hadoop笔记

## 节点规划

三台服务器， NameNode HA ， RM HA ， 三台ZK

Hadoop 版本  hadoop-3.1.3

 Zookeeper版本 zookeeper-3.1.2

| 节点  | 服务                                                         |
| ----- | ------------------------------------------------------------ |
| node1 | **（master）** org.apache.hadoop.hdfs.server.namenode.NameNode<br/>2676 org.apache.hadoop.yarn.server.nodemanager.NodeManager<br/>2280 org.apache.hadoop.hdfs.qjournal.server.JournalNode<br/>1610 org.apache.zookeeper.server.quorum.QuorumPeerMain<br/>2490 org.apache.hadoop.hdfs.tools.DFSZKFailoverController<br/>2011 org.apache.hadoop.hdfs.server.datanode.DataNode |
| node2 | 1698 org.apache.hadoop.hdfs.server.datanode.DataNode<br/>1796 org.apache.hadoop.hdfs.qjournal.server.JournalNode<br/>2391 org.apache.hadoop.yarn.server.nodemanager.NodeManager<br/>**（master）** org.apache.hadoop.yarn.server.resourcemanager.ResourceManager<br/>1599 org.apache.zookeeper.server.quorum.QuorumPeerMain |
| node3 | **（standby）**org.apache.hadoop.hdfs.server.namenode.NameNode<br/>2018 org.apache.hadoop.hdfs.tools.DFSZKFailoverController<br/>1779 org.apache.hadoop.hdfs.server.datanode.DataNode<br/>2436 org.apache.hadoop.mapreduce.v2.hs.JobHistoryServer<br/>1911 org.apache.hadoop.hdfs.qjournal.server.JournalNode<br/>**（standby）**org.apache.hadoop.yarn.server.resourcemanager.ResourceManager<br/>1608 org.apache.zookeeper.server.quorum.QuorumPeerMain<br/>2269 org.apache.hadoop.yarn.server.nodemanager.NodeManager |



## 安装步骤

### 配置文件

#### 1. hadoop-env.sh

```shell
# The java implementation to use. By default, this environment
# variable is REQUIRED on ALL platforms except OS X!
# export JAVA_HOME=
export JAVA_HOME=/usr/local/java/jdk1.8.0_291

# Location of Hadoop.  By default, Hadoop will attempt to determine
# this location based upon its execution path.
export HADOOP_HOME=/opt/software/hadoop-3.1.3
```

#### 2. core-site.xml 	

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!-- Put site-specific property overrides in this file. -->

<configuration>

  
  <property>
  <name>fs.defaultFS</name>
  <value>hdfs://myhadoop</value>
</property>

  <!-- hadoop data dir --> 
  <property>
    <name>hadoop.tmp.dir</name>
	<value>/opt/software/hadoop-3.1.3/data</value>
  </property>
  
  <property>
        <name>ha.zookeeper.quorum</name>
        <value>zookeeper1:2181,zookeeper2:2181,zookeeper3:2181</value>
  </property>
  
  <!-- HDFS web static user -->
  <property>
    <name>hadoop.http.staticuser.user</name>
	<value>root</value>
  </property>
  
    <!-- HDFS trash recycle -->
  <property>
    <name>fs.trash.interval</name>
	<value>10</value>
  </property>
    
     <property>
        <name>hadoop.proxyuser.root.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.root.groups</name>
        <value>*</value>
    </property>
    
    <property>
        <name>hadoop.proxyuser.hive.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.hive.groups</name>
        <value>*</value>
    </property>
    
        <property>
        <name>hadoop.proxyuser.hdfs.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.hdfs.groups</name>
        <value>*</value>
    </property>
    
    <property>
        <name>hadoop.proxyuser.yarn.hosts</name>
        <value>*</value>
    </property>
    <property>
        <name>hadoop.proxyuser.yarn.groups</name>
        <value>*</value>
    </property>
</configuration>
```

#### 3. 启hdfs-site.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration>       
    <property>
        <name>dfs.nameservices</name>
        <value>myhadoop</value>
    </property>
  
   <property>
        <name>dfs.ha.namenodes.myhadoop</name>
        <value>nn1,nn2</value>
    </property>
 
 
    <property>
        <name>dfs.namenode.rpc-address.myhadoop.nn1</name>
        <value>node1:8020</value>
    </property>
 
    <property>
        <name>dfs.namenode.rpc-address.myhadoop.nn2</name>
        <value>node3:8020</value>
    </property>
 
    <property>
        <name>dfs.namenode.http-address.myhadoop.nn1</name>
        <value>node1:9870</value>
    </property>
    <property>
        <name>dfs.namenode.http-address.myhadoop.nn2</name>
        <value>node3:9870</value>
    </property>
 
    <property>
        <name>dfs.namenode.shared.edits.dir</name>
        <value>qjournal://zookeeper1:8485;zookeeper2:8485;zookeeper3:8485/myhadoop</value>
    </property>
 
    <property>
        <name>dfs.journalnode.edits.dir</name>
        <value>/opt/hadoop-3.1.3/journal</value>
    </property>
 
    <property>
        <name>dfs.client.failover.proxy.provider.myhadoop</name>
        <value>org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider</value>
    </property>
 
    <property>
        <name>dfs.ha.fencing.methods</name>
        <value>sshfence</value>
    </property>
 
    <property>
        <name>dfs.ha.fencing.ssh.private-key-files</name>
        <value>/root/.ssh/id_rsa</value>
    </property>
 
    <property>
        <name>dfs.ha.automatic-failover.enabled</name>
        <value>true</value>
    </property>
 
    <property>
        <name>ha.zookeeper.quorum</name>
        <value>zookeeper1:2181,zookeeper2:2181,zookeeper3:2181</value>
    </property>
 
    <property> 
        <name>dfs.replication</name>
        <value>3</value>
    </property>

  <property>
    <name>dfs.namenode.name.dir</name>
	<value>file:///${hadoop.tmp.dir}/dfs/name1,file:///${hadoop.tmp.dir}/dfs/name2</value>
  </property>
	
  <property>
    <name>dfs.datanode.data.dir</name>
	<value>file:///${hadoop.tmp.dir}/dfs/node1,file:///${hadoop.tmp.dir}/dfs/node2,file:///${hadoop.tmp.dir}/dfs/node3</value>
  </property>
 
    <property>
        <name>dfs.webhdfs.enabled</name>
        <value>ture</value>
    </property>
    
   <property>
      	<name>yarn.app.mapreduce.am.staging-dir</name>
      	<value>/tmp/hadoop-yarn/staging</value>
    </property> 

  
</configuration>
```

#### 4.mapred-site.xml

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->

<!-- Put site-specific property overrides in this file. -->

<configuration> 
    <property>
      <name>mapreduce.framework.name</name>
      <value>yarn</value>
    </property>
	
	<property>
        <name>mapreduce.application.classpath</name>
        <value>
         /opt/software/hadoop-3.1.3/etc/hadoop,
         /opt/software/hadoop-3.1.3/share/hadoop/common/*,
         /opt/software/hadoop-3.1.3/share/hadoop/common/lib/*,
         /opt/software/hadoop-3.1.3/share/hadoop/hdfs/*,
         /opt/software/hadoop-3.1.3/share/hadoop/hdfs/lib/*,
         /opt/software/hadoop-3.1.3/share/hadoop/mapreduce/*,
         /opt/software/hadoop-3.1.3/share/hadoop/mapreduce/lib/*,
         /opt/software/hadoop-3.1.3/share/hadoop/yarn/*,
         /opt/software/hadoop-3.1.3/share/hadoop/yarn/lib/*
        </value>
    </property>
	
	<property>
		<name>mapreduce.jobhistory.address</name>
		<value>node3:10020</value>
	</property>
	<property>
		<name>mapreduce.jobhistory.webapp.address</name>
		<value>node3:19888</value>
	</property>
	
	<property>
		<name>yarn.app.mapreduce.am.env</name>
		<value>HADOOP_MAPRED_HOME=/opt/software/hadoop-3.1.3</value>
	</property>
	<property>
	<name>mapreduce.map.env</name>
	<value>HADOOP_MAPRED_HOME=/opt/software/hadoop-3.1.3</value>
	</property>

	<property>
	<name>mapreduce.reduce.env</name>
	<value>HADOOP_MAPRED_HOME=/opt/software/hadoop-3.1.3</value>
	</property>
</configuration>
```

#### 5.yarn-site.xml

```xml
<?xml version="1.0"?>
<!--
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License. See accompanying LICENSE file.
-->
<configuration>

<!-- Site specific YARN configuration properties -->
    <property>
      <name>yarn.nodemanager.aux-services</name>
      <value>mapreduce_shuffle</value>
    </property>
	
	 <property>
        <name>yarn.resourcemanager.ha.enabled</name>
        <value>true</value>
	</property>
	
	<property>
        <name>yarn.resourcemanager.ha.automatic-failover.enabled</name>
        <value>true</value>
    </property>
 
    <property>
        <name>yarn.resourcemanager.cluster-id</name>
        <value>myyarn</value>
    </property>

 <property>
        <name>yarn.resourcemanager.ha.rm-ids</name>
        <value>rm1,rm2</value>
    </property>
 
    <property>
        <name>yarn.resourcemanager.hostname.rm1</name>
        <value>node2</value>
    </property>
 
    <property>
        <name>yarn.resourcemanager.hostname.rm2</name>
        <value>node3</value>
    </property>
 
    <property>
        <name>yarn.resourcemanager.zk-address</name>
        <value>zookeeper1:2181,zookeeper2:2181,zookeeper3:2181</value>
    </property>
 
    <property>
        <name>yarn.resourcemanager.store.class</name>
        <value>org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore</value>
    </property>
 
    <property>
        <name>yarn.resourcemanager.webapp.address.rm1</name>
        <value>node2:8088</value>
    </property>
    <property>
        <name>yarn.resourcemanager.webapp.address.rm2</name>
        <value>node3:8088</value>
    </property>
  
	<property>
		<name>yarn.log-aggregation-enable</name>
		<value>true</value>
	</property>
	
	<property>
		<name>yarn.log.server.url</name>
		<value>http://node2:19888/jobhistory/logs</value>
	</property>

	<property>
		<name>yarn.log-aggregation.retain-seconds</name>
		<value>604800</value>
	</property>
	
	<property>
       <name>yarn.nodemanager.vmem-check-enabled</name>
       <value>false</value>
     </property>
    
	<property>
      <name>yarn.nodemanager.env-whitelist</name>
      <value>JAVA_HOME,HADOOP_COMMON_HOME,HADOOP_HDFS_HOME,HADOOP_CONF_DIR,CLASSPATH_PREPEND_DISTCACHE,HADOOP_YARN_HOME, HADOOP_MAPRED_HOME </value>
    </property>
	
	<property>
        <name>yarn.application.classpath</name>
        <value>/opt/software/hadoop-3.1.3/etc/hadoop:/opt/software/hadoop-3.1.3/share/hadoop/common/lib/*:/opt/software/hadoop-3.1.3/share/hadoop/common/*:/opt/software/hadoop-3.1.3/share/hadoop/hdfs:/opt/software/hadoop-3.1.3/share/hadoop/hdfs/lib/*:/opt/software/hadoop-3.1.3/share/hadoop/hdfs/*:/opt/software/hadoop-3.1.3/share/hadoop/mapreduce/lib/*:/opt/software/hadoop-3.1.3/share/hadoop/mapreduce/*:/opt/software/hadoop-3.1.3/share/hadoop/yarn:/opt/software/hadoop-3.1.3/share/hadoop/yarn/lib/*:/opt/software/hadoop-3.1.3/share/hadoop/yarn/*</value>
	</property>
</configuration>
```

#### 6.workers

```shell
node1
node2
node3
```

以上配置拷贝到所有集群上，配置完成，若需要添加其他配置参数，参考官网：https://hadoop.apache.org/docs/r3.1.3/hadoop-project-dist/hadoop-common/ClusterSetup.html

### 格式化相关操作（前提zookeeper集群已启动）

#### 1. 首次手动启动

```shell
1.在指定的所有journalnode机器上执行命令启动journalnode

   hdfs --daemon start journalnode

2.在某台机器上执行namenode格式化（zk进程所在节点）

   hdfs namenode -format

3.在所在某一台namenode机器执行，启动namenode

   hdfs --daemon start namenode

4.在其余namenode机器上执行，同步active namenode信息，作为secondarynamenode

   hdfs namenode -bootstrapStandby

5.在active namenode所在节点执行，初始化zookeeper上NameNode的状态

  hdfs zkfc -formatZK

6.start-dfs.sh 启动ha，在指定的resource manager所在机器上执行start-yarn.sh启动resourcemanager、nodemanager
```

#### 2. myHadoopCluster.sh（非首次启动shell ）

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
echo " 启动 zookeeper 集群 "

ssh node1 "source /etc/profile;/opt/software/zookeeper/bin/zkServer.sh start"
ssh node2 "source /etc/profile;/opt/software/zookeeper/bin/zkServer.sh start"
ssh node3 "source /etc/profile;/opt/software/zookeeper/bin/zkServer.sh start"

echo " 启动 hadoop 集群 "

echo " 启动 hdfs"
ssh node1 "source /etc/profile;/opt/software/hadoop-3.1.3/sbin/start-dfs.sh"

echo " 启动 yarn "
ssh node2 "source /etc/profile;/opt/software/hadoop-3.1.3/sbin/start-yarn.sh"

echo "启动 historyserver "
ssh node3 "source /etc/profile;/opt/software/hadoop-3.1.3/bin/mapred --daemon start historyserver "
;;
"stop")
echo "关闭hadoop集群"

echo "关闭 historyserver "
ssh node3 "source /etc/profile;/opt/software/hadoop-3.1.3/bin/mapred --daemon stop historyserver"

echo " 关闭 yarn "
ssh node2 "source /etc/profile;/opt/software/hadoop-3.1.3/sbin/stop-yarn.sh"

echo " 关闭 hdfs"
ssh node1 "source /etc/profile;/opt/software/hadoop-3.1.3/sbin/stop-dfs.sh"

echo " 关闭zookeeper"
ssh node1 "source /etc/profile;/opt/software/zookeeper/bin/zkServer.sh stop"
ssh node2 "source /etc/profile;/opt/software/zookeeper/bin/zkServer.sh stop"
ssh node3 "source /etc/profile;/opt/software/zookeeper/bin/zkServer.sh stop"
;;
*)
        echo "Input Args Error..."
;;
esac
```

#### 4. jpsall （查看所有节点的服务启动）

```shell
#!/bin/bash
for host in node1 node2 node3
do
        echo =============== $host ===============
        ssh $host "/usr/local/java/jdk1.8.0_291/bin/jps -l"
done
```

#### 5. 使用脚本启停服务

```powershell
# 停止集群服务
[root@node1 hadoop]# myHadoopCluster.sh stop

# 启动集群服务
[root@node1 hadoop]# myHadoopCluster.sh start
```

#### 6. 浏览器访问

```powershell
hdfs active web url：http://node1:9870
hdfs stanbdy web url：http://node3:9870

resourcemanager web url：http://node2:8088

JobHistory web url： http://node3:19888/jobhistory/app
```

#### 7. hosts 配置 

```shell
[root@node1 hadoop]# cat /etc/hosts
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6
192.168.2.100 node1
192.168.2.101 node2
192.168.2.102 node3
192.168.2.100 zookeeper1
192.168.2.101 zookeeper2
192.168.2.102 zookeeper3
```

#### 8. static IP配置

```shell
# HWADDR 在虚拟机clone时候 ， 会自动生产新的 

# uuid 使用uuidgen随机生成
[root@node1 hadoop]# uuidgen
ae4f3c75-ddbd-4050-b2aa-6c087354ff1c
[root@node1 hadoop]# uuidgen
8114e71a-45b5-462a-bcd0-c47f2be2d17a

[root@node1 hadoop]# cat /etc/sysconfig/network-scripts/ifcfg-ens33
TYPE=Ethernet
PROXY_METHOD=none
BROWSER_ONLY=no
BOOTPROTO=static
DEFROUTE=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=ens33
DEVICE=ens33
ONBOOT=yes
IPADDR=192.168.2.100
NETMASK=255.255.255.0
GATEWAY=192.168.2.1
DNS1=192.168.2.1
HWADDR=00:0C:29:64:87:2F
UUID=4f3db4f7-687d-4267-943b-745a998a60ab


[root@node2 ~]# cat /etc/sysconfig/network-scripts/ifcfg-ens33
TYPE=Ethernet
PROXY_METHOD=none
BROWSER_ONLY=no
BOOTPROTO=static
DEFROUTE=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=ens33
DEVICE=ens33
ONBOOT=yes
IPADDR=192.168.2.101
GATEWAY=192.168.2.1
DNS1=192.168.2.1
HWADDR=00:0C:29:EB:8E:9C
UUID=d1097870-89d7-439d-8b03-cb53b535748d

[root@node3 ~]# cat /etc/sysconfig/network-scripts/ifcfg-ens33
TYPE=Ethernet
PROXY_METHOD=none
BROWSER_ONLY=no
BOOTPROTO=static
DEFROUTE=yes
IPV4_FAILURE_FATAL=no
IPV6INIT=yes
IPV6_AUTOCONF=yes
IPV6_DEFROUTE=yes
IPV6_FAILURE_FATAL=no
IPV6_ADDR_GEN_MODE=stable-privacy
NAME=ens33
DEVICE=ens33
ONBOOT=yes
IPADDR=192.168.2.102
GATEWAY=192.168.2.1
DNS1=192.168.2.1
HWADDR=00:0C:29:4D:3B:C7
UUID=a3c02619-c8c7-4ea3-a3e2-59bc83e8de16
```





### 问题解决

##### CentOS8 ifconfig 没有ens33方案

```shell
# 查看托管状态
[root@node1 hadoop-3.1.3]# nmcli n
enabled

显示 disabled 则为本文遇到的问题，如果是 enabled 则可以不用往下看了

# 开启 托管
[root@node1 hadoop-3.1.3]# nmcli n on

# 重启
[root@node1 hadoop-3.1.3]# systemctl restart NetworkManager

# 查看NetworkManager 状态
[root@node1 hadoop-3.1.3]# systemctl status NetworkManager
● NetworkManager.service - Network Manager
   Loaded: loaded (/usr/lib/systemd/system/NetworkManager.service; enabled; vendor preset: enabled)
   Active: active (running) since Mon 2021-05-31 09:57:07 CST; 4h 45min ago
     Docs: man:NetworkManager(8)
 Main PID: 920 (NetworkManager)
    Tasks: 3 (limit: 23500)
   Memory: 11.7M
   CGroup: /system.slice/NetworkManager.service
           └─920 /usr/sbin/NetworkManager --no-daemon

# 临时关闭
systemctl stop NetworkManager     
# 永久关闭网络管理命令
systemctl disable NetworkManager    
# 开启网络服务
systemctl start NetworkManager                          

```

