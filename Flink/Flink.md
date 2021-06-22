



# Flink笔记

目录： 

[toc]

### Flink 下载地址

#### 1. 官网下载（推荐）

1.1 最新1.13.1版本

https://flink.apache.org/downloads.html

### 中文官方文档

https://ci.apache.org/projects/flink/flink-docs-release-1.13/zh/docs/

### 上传解压

为了运行Flink，只需提前安装好 **Java 8 或者 Java 11**。你可以通过以下命令来检查 Java 是否已经安装正确。

/opt/software/flink-1.13.1

```shell
$ tar -xzf flink-{{ site.version }}-bin-scala_2.11.tgz
$ cd flink-{{ site.version }}-bin-scala_2.11
```

### 启动集群

```shell
[root@node1 flink-1.13.1]# bin/start-cluster.sh
Starting cluster.
Starting standalonesession daemon on host node1.
Starting taskexecutor daemon on host node2.
Starting taskexecutor daemon on host node3.
```

### Web UI

```shell
http://node1:8081/#/overview
http://192.168.2.100:8081/#/overview
```





### Build with Sugon Cluster Version

hadoop 3.1.1 
zookeeper 3.4.6
hive 3.1.0
hbase 2.1.7 
spark 2.3.4
kafka 2.4.1

Spark 3.0 集成Flink 0.8   POM XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>SparkAPI</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
       <!--  Spark 3.0 集成Flink 0.8 -->
        <scala.version>2.12</scala.version>
        <scala.binary.version>2.12.12</scala.binary.version>
        <spark.version>3.0.0</spark.version>
        <hoodie.version>0.8.0</hoodie.version>
        <hadoop.version>3.1.3</hadoop.version>
        <hive.version>3.1.2</hive.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-common</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.parquet</groupId>
            <artifactId>parquet-hive-bundle</artifactId>
            <version>1.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-spark-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-spark3-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-hadoop-mr-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
<!--        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-hive-sync-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>-->

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-avro_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
      </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.8</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.10.0</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-auth</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.binary.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- 该插件将scala代码编译成class文件 -->
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>


            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```
Spark 2.4.5 集成Flink 0.5.3

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>SparkAPI</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
       <!--  Spark 2.4.5 集成Flink 0.5.3 -->
        <scala.version>2.11</scala.version>
        <scala.binary.version>2.11.12</scala.binary.version>
        <spark.version>2.4.5</spark.version>
        <hoodie.version>0.5.3</hoodie.version>
        <hadoop.version>3.1.3</hadoop.version>
        <hive.version>3.1.2</hive.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-client</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-common</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-spark-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-hadoop-mr-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
<!--        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-hive-sync-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>-->

        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-avro_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
      </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-auth</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.scala-lang</groupId>
            <artifactId>scala-library</artifactId>
            <version>${scala.binary.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- 该插件将scala代码编译成class文件 -->
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>


            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```

再次执行打包命令

```
mvn clean package -DskipTests -Dspark3
```

### **mvn 报错**

#### 1. Flink-hadoop-mr 缺少org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde 包

```shell
# 详细报错信息 
[ERROR] Failed to execute goal on project Flink-hadoop-mr: Could not resolve dependencies for project org.apache.Flink:Flink-hadoop-mr:jar:0.9.0-SNAPSHOT: Could not find artifact org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde in maven-ali (http://maven.aliyun.com/nexus/content/groups/public//) -> [Help 1]
```

解决办法 ：

1. 手动下载此jar包

pentaho 中央仓库

下载地址1： https://public.nexus.pentaho.org/repository/proxy-public-3rd-party-release/org/pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde/pentaho-aggdesigner-algorithm-5.1.5-jhyde.jar

MVN 公共仓库：

下载地址2：https://mvnrepository.com/artifact/org.pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde

2. 将手动下载的jar移动到本地maven仓库地址中(推荐)

   需要先在Flink 目录下执行一次mvn clean package -DskipTests ， 才能生存pentaho\pentaho-aggdesigner-algorithm\5.1.5-jhyde目录，之后再把jar包移进去

   本地maven仓库地址：C:\Users\49921\.m2\repository\org\pentaho\pentaho-aggdesigner-algorithm\5.1.5-jhyde

3. 或者手动install 本地jar包到本地仓库中

   -Dfile 为本地jar路径

   mvn install:install-file -DgroupId=org.pentaho -DartifactId=pentaho-aggdesigner-algorithm -Dversion=5.1.5-jhyde -Dpackaging=jar -Dfile=C:/users/49921/Desktop/pentaho-aggdesigner-algorithm-5.15-jhyde.jar

   ###### mvn install:install-file将本地一个中央仓库没有的jar包，推到本地仓库

   ###### 参考链接 ： https://www.cnblogs.com/daofree/p/12681855.html

   

#### 2. window环境下注释掉 Flink-integ-test和Flink-integ-test-bundle

```shell
# 报错信息
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.6.0:exec (Setup Flink_WS) on project Flink-integ-test: Command execution failed.: Cannot run program "\bin\bash" (in directory "C:\Users\49921\Desktop\Flink-release-0.8.0\Flink-release-0.8.0\Flink-integ-test"): CreateProcess error=2, 系统找不到指定的文件。 -> [Help 1]
```

**解决办法**

POM 文件注释报错的相关模块

```xml
 <modules>
    <module>Flink-common</module>
    <module>Flink-cli</module>
    <module>Flink-client</module>
    <module>Flink-hadoop-mr</module>
    <module>Flink-spark-datasource</module>
    <module>Flink-timeline-service</module>
    <module>Flink-utilities</module>
    <module>Flink-sync</module>
    <module>packaging/Flink-hadoop-mr-bundle</module>
    <module>packaging/Flink-hive-sync-bundle</module>
    <module>packaging/Flink-spark-bundle</module>
    <module>packaging/Flink-presto-bundle</module>
    <module>packaging/Flink-utilities-bundle</module>
    <module>packaging/Flink-timeline-server-bundle</module>
    <module>docker/hoodie/hadoop</module>
    <!--  如下两个test模块注释掉
	<module>Flink-integ-test</module>
    <module>packaging/Flink-integ-test-bundle</module>
	-->
    <module>Flink-examples</module>
    <module>Flink-flink</module>
    <module>packaging/Flink-flink-bundle</module>
  </modules>
```

#### 3. Failure to find org.glassfish:javax.el:pom:3.0.1-b06-SNAPSHOT in 

**解决办法**

找到本地maven仓库 ： C:\Users\49921\.m2\repository\org\glassfish\javax.el\3.0.1-b11-SNAPSHOT

把pom.lastupdate文件名修改成.pom ， 每一个版本下的SNAPSHOT名字都要改，然后idea 重新导包

## Flink 集成Hive

1. 将Flink-hadoop-mr-bundle-0.8.0.jar 拷贝到 Hive lib下

```shell
[root@node1 ~]# scp Flink-hadoop-mr-bundle-0.8.0.jar /opt/software/hive-3.1.2/lib/
```

2. 重启Hive服务 

   HiveServer2 和  HiveMetastore 两个服务

```shell
[root@node1 hive]# /home/hive/hiveservices.sh restart
[root@node1 hive]# /home/hive/hiveservices.sh status
node1 Metastore服务运行正常
node1 HiveServer2服务运行异常

# 查看进程也可以确定是否服务重启成功
[root@node1 hive]# ps -ef | grep hive
```

3. 进入Hive Client

```shell
[root@node1 hive-3.1.2]# /opt/software/hive-3.1.2/bin/hive
```

POM.XML

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>SparkAPI</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
       <!--  Spark 3.0 集成Flink 0.8 -->
        <scala.version>2.12</scala.version>
        <scala.binary.version>2.12.12</scala.binary.version>
        <spark.version>3.0.0</spark.version>
        <hoodie.version>0.8.0</hoodie.version>
        <hadoop.version>3.1.3</hadoop.version>
        <hive.version>3.1.2</hive.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-common</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.parquet</groupId>
            <artifactId>parquet-hive-bundle</artifactId>
            <version>1.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-spark3-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-hadoop-mr-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.Flink</groupId>
            <artifactId>Flink-hive-sync-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-avro_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-sql_${scala.version}</artifactId>
            <version>${spark.version}</version>
      </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.9.8</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.10.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-auth</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-hdfs</artifactId>
            <version>${hadoop.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-jdbc</artifactId>
            <version>${hive.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-exec</artifactId>
            <version>${hive.version}</version>
        </dependency>
        <dependency>
            <groupId>org.spark-project.hive</groupId>
            <artifactId>hive-serde</artifactId>
            <version>1.2.1.spark2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-hive_${scala.version}</artifactId>
            <version>${spark.version}</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <!-- 该插件将scala代码编译成class文件 -->
            <plugin>
                <groupId>net.alchim31.maven</groupId>
                <artifactId>scala-maven-plugin</artifactId>
                <version>3.2.2</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>compile</goal>
                            <goal>testCompile</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>


            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.3.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

