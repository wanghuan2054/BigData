



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

https://spark.apache.org/docs/latest/api/python/getting_started/install.html

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

### PySpark 

User Guide

https://spark.apache.org/docs/latest/sql-getting-started.html

https://spark.apache.org/docs/latest/api/python/getting_started/quickstart.html

```shell
# 上传数据文件
[root@node1 spark-3.0.0-hadoop3.2]# hadoop fs -put examples/src/main/resources/*.json /tmp
# 进入pyspark shell
[root@node1 spark-3.0.0-hadoop3.2]# bin/pyspark


from pyspark.sql import SparkSession

spark = SparkSession \
    .builder \
    .appName("Python Spark SQL basic example") \
    .config("spark.some.config.option", "some-value") \
    .getOrCreate()
```

### Build with Spark 3.0.0

The default Spark version supported is 2.4.4. To build for Spark 3.0.0 version, build using `spark3` profile

Hadoop version supported is  2.7.3

```
mvn clean package -DskipTests -Dspark3
```
