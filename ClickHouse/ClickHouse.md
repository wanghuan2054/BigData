



# ClickHouse笔记

目录： 

[toc]

### **依赖环境**

```markdown
CentOS Linux release 8.3.2011
Hadoop 3.1.3
Hive 3.1.2
Python 3.6
java version "1.8.0_291"
```

### **验证sse 4.2是否支持**

```shell
[root@slave1 ~]# grep -q sse4_2 /proc/cpuinfo && echo "SSE 4.2 supported" || echo "SSE 4.2 not supported“"
SSE 4.2 supported
```

### **防火墙检查**

```shell
systemctl start firewalld		#启动防火墙服务
systemctl stop firewalld		#停止防火墙服务
systemctl restart firewalld		#重启防火墙服务
systemctl status firewalld		#检查防火墙服务状态
systemctl enable firewalld		#设防火墙为开机启动
systemctl disable firewalld		#禁止防火墙开机启动
```

### **CentOS 取消 SELINUX **

修改/etc/selinux/config 中的 SELINUX=disabled

```shell
[root@node1 ~]#  vim /etc/selinux/config

# This file controls the state of SELinux on the system.
# SELINUX= can take one of these three values:
#     enforcing - SELinux security policy is enforced.
#     permissive - SELinux prints warnings instead of enforcing.
#     disabled - No SELinux policy is loaded.
SELINUX=disabled
# SELINUXTYPE= can take one of these three values:
#     targeted - Targeted processes are protected,
#     minimum - Modification of targeted policy. Only selected processes are protected.
#     mls - Multi Level Security protection.
SELINUXTYPE=targeted

# 同步至其他节点
[root@node1 ~]# rsync -av /etc/selinux/config root@node2:/etc/selinux/
[root@node1 ~]# rsync -av /etc/selinux/config root@node3:/etc/selinux/
```

### **CentOS 取消打开文件数限制**

```shell
#  /etc/security/limits.conf 文件的末尾加入以下内容
[root@node1 ~]# vim /etc/security/limits.conf
* soft nofile 65536
* hard nofile 65536
* soft nproc 131072
* hard nproc 131072

# 该文件不存在 ， 则创建
[root@node1 ~]# vim /etc/security/limits.d/20-nproc.conf
* soft nofile 65536
* hard nofile 65536
* soft nproc 131072
* hard nproc 131072

# 执行同步操作 ， 同步文件至其它节点
rsync -av  /etc/security/limits.conf root@node2:/etc/security/
rsync -av  /etc/security/limits.conf root@node3:/etc/security/
rsync -av  /etc/security/limits.d/20-nproc.conf  root@node3:/etc/security/limits.d/
rsync -av  /etc/security/limits.d/20-nproc.conf  root@node2:/etc/security/limits.d/

```

**上述操作完成后， 需要重启集群服务器**

### **ClickHouse 安装步骤**

#### **单机安装**

官网：https://clickhouse.yandex/
下载地址：https://repo.clickhouse.tech/rpm/stable/x86_64/

学习文档 ： https://clickhouse.tech/docs/en/getting-started/install/

#### **目录创建**

**/opt/software 下 创 建** clickhouse 目录

```shell
[root@node1 ~]# cd /opt/software/
[root@node1 software]# mkdir clickhouse

[root@node1 software]# ls
clickhouse  flink-1.13.1  hive-3.1.2   kafka-2.8.0            presto
```

### **离线安装**

rpm包下载上传至 /opt/software/clickhouse目录下

```shell
-rw-r--r-- 1 root root     62999 6月  10 08:55 clickhouse-client-21.6.3.14-2.noarch.rpm
-rw-r--r-- 1 root root 163474785 6月  10 08:55 clickhouse-common-static-21.6.3.14-2.x86_64.rpm
-rw-r--r-- 1 root root 766767062 6月  10 08:55 clickhouse-common-static-dbg-21.6.3.14-2.x86_64.rpm
-rw-r--r-- 1 root root     87445 6月  10 08:55 clickhouse-server-21.6.3.14-2.noarch.rpm

# 将/opt/software/clickhouse目录分发至其它节点
[root@node1 software]# scp -r clickhouse root@node2:/opt/software/
[root@node1 software]# scp -r clickhouse root@node3:/opt/software/
```

##### 安装rpm

在每个节点分别执行

```shell
# 安装过程中提示输入user密码 ， 默认回车不输入即可
[root@node1 clickhouse]# rpm -ivh *.rpm

# 查看安装情况
[root@node1 clickhouse]# rpm -qa | grep clickhouse
clickhouse-server-21.6.3.14-2.noarch
clickhouse-client-21.6.3.14-2.noarch
clickhouse-common-static-dbg-21.6.3.14-2.x86_64
clickhouse-common-static-21.6.3.14-2.x86_64
```

##### 修改配置文件

在这个文件中，有 ClickHouse 的一些默认路径配置，比较重要的

数据文件路径：<path>/var/lib/clickhouse/</path>

日志文件路径：<log>/var/log/clickhouse-server/clickhouse-server.log</log>

```shell
[root@node1 clickhouse]# vim /etc/clickhouse-server/config.xml
# 把 <listen_host>::</listen_host> 的注释打开，这样的话才能让 ClickHouse 被除本机以外的服务器访问
<listen_host>::</listen_host>

# 分发配置文件
[root@node1 clickhouse]# rsync -av /etc/clickhouse-server/config.xml root@node2:/etc/clickhouse-server/
[root@node1 clickhouse]# rsync -av /etc/clickhouse-server/config.xml root@node3:/etc/clickhouse-server/
```

### **在线安装**

```shell
sudo yum install yum-utils
sudo rpm --import https://repo.clickhouse.tech/CLICKHOUSE-KEY.GPG
sudo yum-config-manager --add-repo https://repo.clickhouse.tech/rpm/stable/x86_64
sudo yum install clickhouse-server clickhouse-client
```

### **启停 clickhouse**

```shell
# 启动 
[root@node1 clickhouse]#  systemctl start clickhouse-server
# 查看状态
[root@node1 clickhouse]# systemctl status clickhouse-server
# 停止
[root@node1 clickhouse]# systemctl stop clickhouse-server
# 关闭开机自启
[root@node1 clickhouse]# systemctl disable clickhouse-server
# 开启开机自启
[root@node1 clickhouse]# systemctl enable clickhouse-server
```

### **client 连接 server**

```shell
# -m :可以在命令窗口输入多行命令
[root@node1 clickhouse]#  clickhouse-client -m
ClickHouse client version 21.6.3.14 (official build).
Connecting to localhost:9000 as user default.
Connected to ClickHouse server version 21.6.3 revision 54448.

node1 :) show databases;

SHOW DATABASES

Query id: 866882b7-6030-41c1-86a7-478bd3fd521d

┌─name────┐
│ default │
│ system  │
└─────────┘

2 rows in set. Elapsed: 0.002 sec.

node1 :)
```

### **ClickHouse SQL**

```shell
node1 :) create database sugon ;


node1 :) show databases;

┌─name────┐
│ default │
│ sugon   │
│ system  │
└─────────┘

node1 :) use sugon ;


node1 :) CREATE TABLE t_enum
:-] (
:-]  x Enum8('hello' = 1, 'world' = 2)
:-] )
:-] ENGINE = TinyLog;

CREATE TABLE t_enum
(
    `x` Enum8('hello' = 1, 'world' = 2)
)
ENGINE = TinyLog

node1 :) INSERT INTO t_enum VALUES ('hello'), ('world'), ('hello');
node1 :) select * from  t_enum ;
SELECT *
FROM t_enum

┌─x─────┐
│ hello │
│ world │
│ hello │
└───────┘
# 如果尝试保存任何其他值，ClickHouse 抛出异常
```
