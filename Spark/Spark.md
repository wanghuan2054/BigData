



# Spark笔记

目录： 

[toc]

### Spark 下载地址

#### 1. Apache Dist下载

Spark 3.0 下载

下载地址：http://archive.apache.org/dist/spark/spark-3.0.0/

### **Spark Local模式**

```shell
# 安装路径 /opt/software
[root@node1 software]# tar -zxvf spark-3.0.0-bin-hadoop3.2.tgz

[root@node1 software]# mv spark-3.0.0-bin-hadoop3.2/ spark-3.0.0-hadoop3.2/

# 启动Local模式
[root@node1 software]# cd spark-3.0.0-hadoop3.2/
[root@node1 spark-3.0.0-hadoop3.2]# ls
bin  conf  data  examples  jars  kubernetes  LICENSE  licenses  NOTICE  python  R  README.md  RELEASE  sbin  yarn
root@node1 spark-3.0.0-hadoop3.2]# bin/spark-shell
21/06/07 19:09:35 WARN NativeCodeLoader: Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
Using Spark's default log4j profile: org/apache/spark/log4j-defaults.properties
Setting default log level to "WARN".
To adjust logging level use sc.setLogLevel(newLevel). For SparkR, use setLogLevel(newLevel).
Spark context Web UI available at http://node1:4040
Spark context available as 'sc' (master = local[*], app id = local-1623064192356).
Spark session available as 'spark'.
Welcome to
      ____              __
     / __/__  ___ _____/ /__
    _\ \/ _ \/ _ `/ __/  '_/
   /___/ .__/\_,_/_/ /_/\_\   version 3.0.0
      /_/

Using Scala version 2.12.10 (Java HotSpot(TM) 64-Bit Server VM, Java 1.8.0_291)
Type in expressions to have them evaluated.
Type :help for more information.


# 启动成功后，可以输入网址进行 Web UI 监控页面访问
http://node1:4040/
```

## Pyspark 安装

```shell
[root@node1 data]# pip3 install pyspark
```

打包之后的jar包位置：C:\Users\49921\Desktop\Spark-release-0.8.0\Spark-release-0.8.0\packaging 

具体Spark与spark集成jar 位于 ：C:\Users\49921\Desktop\Spark-release-0.8.0\Spark-release-0.8.0\packaging\Spark-spark-bundle\target



To build the Javadoc for all Java and Scala classes:

```
# Javadoc generated under target/site/apidocs
mvn clean javadoc:aggregate -Pjavadocs
```

### Build with Scala 2.12

The default Scala version supported is 2.11. To build for Scala 2.12 version, build using `scala-2.12` profile

```
mvn clean package -DskipTests -Dscala-2.12
```

### Build with Spark 3.0.0

The default Spark version supported is 2.4.4. To build for Spark 3.0.0 version, build using `spark3` profile

Hadoop version supported is  2.7.3

```
mvn clean package -DskipTests -Dspark3
```



### Build with Sugon Cluster Version

hadoop 3.1.1 
zookeeper 3.4.6
hive 3.1.0
hbase 2.1.7 1.2.3
spark 2.3.4
kafka 2.4.1

presto  ？

修改源码中POM文件中的对应组件Version ， 尽量和上述版本一致

```xml
<!-- 修改如下组件版本即可 ，其余保持不动-->
<properties>
    <kafka.version>2.4.1</kafka.version>
    <hadoop.version>3.1.1</hadoop.version>
    <hive.version>3.1.0</hive.version>
    <spark2.version>2.3.4</spark2.version>
    <spark3.version>3.0.0</spark3.version>
    <hbase.version>2.1.7</hbase.version>
</properties>
```
再次执行打包命令

```
mvn clean package -DskipTests
```

### **mvn 报错**

#### 1. Spark-hadoop-mr 缺少org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde 包

```shell
# 详细报错信息 
[ERROR] Failed to execute goal on project Spark-hadoop-mr: Could not resolve dependencies for project org.apache.Spark:Spark-hadoop-mr:jar:0.9.0-SNAPSHOT: Could not find artifact org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde in maven-ali (http://maven.aliyun.com/nexus/content/groups/public//) -> [Help 1]
```

解决办法 ：

1. 手动下载此jar包

pentaho 中央仓库

下载地址1： https://public.nexus.pentaho.org/repository/proxy-public-3rd-party-release/org/pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde/pentaho-aggdesigner-algorithm-5.1.5-jhyde.jar

MVN 公共仓库：

下载地址2：https://mvnrepository.com/artifact/org.pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde

2. 将手动下载的jar移动到本地maven仓库地址中(推荐)

   需要先在Spark 目录下执行一次mvn clean package -DskipTests ， 才能生存pentaho\pentaho-aggdesigner-algorithm\5.1.5-jhyde目录，之后再把jar包移进去

   本地maven仓库地址：C:\Users\49921\.m2\repository\org\pentaho\pentaho-aggdesigner-algorithm\5.1.5-jhyde

3. 或者手动install 本地jar包到本地仓库中

   -Dfile 为本地jar路径

   mvn install:install-file -DgroupId=org.pentaho -DartifactId=pentaho-aggdesigner-algorithm -Dversion=5.1.5-jhyde -Dpackaging=jar -Dfile=C:/users/49921/Desktop/pentaho-aggdesigner-algorithm-5.15-jhyde.jar

   ###### mvn install:install-file将本地一个中央仓库没有的jar包，推到本地仓库

   ###### 参考链接 ： https://www.cnblogs.com/daofree/p/12681855.html

   

#### 2. window环境下注释掉 Spark-integ-test和Spark-integ-test-bundle

```shell
# 报错信息
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.6.0:exec (Setup Spark_WS) on project Spark-integ-test: Command execution failed.: Cannot run program "\bin\bash" (in directory "C:\Users\49921\Desktop\Spark-release-0.8.0\Spark-release-0.8.0\Spark-integ-test"): CreateProcess error=2, 系统找不到指定的文件。 -> [Help 1]
```

### **解决办法**

POM 文件注释报错的相关模块

```xml
 <modules>
    <module>Spark-common</module>
    <module>Spark-cli</module>
    <module>Spark-client</module>
    <module>Spark-hadoop-mr</module>
    <module>Spark-spark-datasource</module>
    <module>Spark-timeline-service</module>
    <module>Spark-utilities</module>
    <module>Spark-sync</module>
    <module>packaging/Spark-hadoop-mr-bundle</module>
    <module>packaging/Spark-hive-sync-bundle</module>
    <module>packaging/Spark-spark-bundle</module>
    <module>packaging/Spark-presto-bundle</module>
    <module>packaging/Spark-utilities-bundle</module>
    <module>packaging/Spark-timeline-server-bundle</module>
    <module>docker/hoodie/hadoop</module>
    <!--  如下两个test模块注释掉
	<module>Spark-integ-test</module>
    <module>packaging/Spark-integ-test-bundle</module>
	-->
    <module>Spark-examples</module>
    <module>Spark-flink</module>
    <module>packaging/Spark-flink-bundle</module>
  </modules>
```

