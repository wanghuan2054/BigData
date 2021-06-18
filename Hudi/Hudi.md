



# Hudi数据湖笔记

目录： 

[toc]

### Hudi 下载地址

#### 1. Github 下载（推荐）

##### 1.1 最新0.9.0 SNAPSHOT 版本

https://github.com/apache/hudi/tree/master

###### 拉取0.9.0  SNAPSHOT 源码

```shell
$ git clone https://github.com/apache/hudi.git 
```

##### 1.2 最新Release 0.8.0 branches 版本

https://github.com/apache/hudi/tree/release-0.8.0

###### 拉取指定分支源码

```shell
$ git clone -b release-0.8.0 git@github.com:apache/hudi.git
```

#### 2. Apache Dist下载

下载地址：https://archive.apache.org/dist/hudi/0.8.0/

### **修改源码pom.xml文件添加阿里云仓库**

```xml
<repositories>
    <!-- 添加如下仓库地址 --> 
    <repository>
        <id>maven-ali</id>
        <url>http://maven.aliyun.com/nexus/content/groups/public//</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>fail</checksumPolicy>
        </snapshots>
    </repository>
    <!-- 添加仓库地址 --> 
	<repository>
      <id>Maven Central</id>
      <name>Maven Repository</name>
      <url>https://repo.maven.apache.org/maven2</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>
    <repository>
      <id>cloudera-repo-releases</id>
      <url>https://repository.cloudera.com/artifactory/public/</url>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
    </repository>
    <repository>
      <id>confluent</id>
      <url>https://packages.confluent.io/maven/</url>
    </repository>
  </repositories>
```

## Building Apache Hudi from source

Prerequisites for building Apache Hudi:

* Unix-like system (like Linux, Mac OS X)
* Java 8 (Java 9 or 10 may work)
* Git
* Maven

```shell
# Checkout code and build
# Hudi 源码下载到 C:\Users\49921\Desktop\
# windows 下进入PowerShell , 进入Hudi 目录下
PS C:\Users\49921\Desktop> cd hudi
PS C:\Users\49921\Desktop\hudi> pwd
Path
----
C:\Users\49921\Desktop\hudi

# 执行mvn 打包命令 , 默认hudi 0.8.0 基于hadoop 2.7.3 ， spark 2.4.4
PS C:\Users\49921\Desktop\hudi> mvn clean package -DskipTests

# Start comm4.4
spark-2.4.4-bin-hadoop2.7/bin/spark-shell \
  --jars `ls packaging/hudi-spark-bundle/target/hudi-spark-bundle_2.11-*.*.*-SNAPSHOT.jar` \
  --conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer'
```

打包之后的jar包位置：C:\Users\49921\Desktop\hudi-release-0.8.0\hudi-release-0.8.0\packaging 

具体hudi与spark集成jar 位于 ：C:\Users\49921\Desktop\hudi-release-0.8.0\hudi-release-0.8.0\packaging\hudi-spark-bundle\target



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
hbase 2.1.7 
spark 2.3.4
kafka 2.4.1

Spark 3.0 集成Hudi 0.8   POM XML

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
       <!--  Spark 3.0 集成Hudi 0.8 -->
        <scala.version>2.12</scala.version>
        <scala.binary.version>2.12.12</scala.binary.version>
        <spark.version>3.0.0</spark.version>
        <hoodie.version>0.8.0</hoodie.version>
        <hadoop.version>3.1.3</hadoop.version>
        <hive.version>3.1.2</hive.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-common</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.parquet</groupId>
            <artifactId>parquet-hive-bundle</artifactId>
            <version>1.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-spark-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-spark3-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-hadoop-mr-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
<!--        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-hive-sync-bundle</artifactId>
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
Spark 2.4.5 集成Hudi 0.5.3

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
       <!--  Spark 2.4.5 集成Hudi 0.5.3 -->
        <scala.version>2.11</scala.version>
        <scala.binary.version>2.11.12</scala.binary.version>
        <spark.version>2.4.5</spark.version>
        <hoodie.version>0.5.3</hoodie.version>
        <hadoop.version>3.1.3</hadoop.version>
        <hive.version>3.1.2</hive.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-client</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-common</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-spark-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-hadoop-mr-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
<!--        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-hive-sync-bundle</artifactId>
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

#### 1. hudi-hadoop-mr 缺少org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde 包

```shell
# 详细报错信息 
[ERROR] Failed to execute goal on project hudi-hadoop-mr: Could not resolve dependencies for project org.apache.hudi:hudi-hadoop-mr:jar:0.9.0-SNAPSHOT: Could not find artifact org.pentaho:pentaho-aggdesigner-algorithm:jar:5.1.5-jhyde in maven-ali (http://maven.aliyun.com/nexus/content/groups/public//) -> [Help 1]
```

解决办法 ：

1. 手动下载此jar包

pentaho 中央仓库

下载地址1： https://public.nexus.pentaho.org/repository/proxy-public-3rd-party-release/org/pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde/pentaho-aggdesigner-algorithm-5.1.5-jhyde.jar

MVN 公共仓库：

下载地址2：https://mvnrepository.com/artifact/org.pentaho/pentaho-aggdesigner-algorithm/5.1.5-jhyde

2. 将手动下载的jar移动到本地maven仓库地址中(推荐)

   需要先在hudi 目录下执行一次mvn clean package -DskipTests ， 才能生存pentaho\pentaho-aggdesigner-algorithm\5.1.5-jhyde目录，之后再把jar包移进去

   本地maven仓库地址：C:\Users\49921\.m2\repository\org\pentaho\pentaho-aggdesigner-algorithm\5.1.5-jhyde

3. 或者手动install 本地jar包到本地仓库中

   -Dfile 为本地jar路径

   mvn install:install-file -DgroupId=org.pentaho -DartifactId=pentaho-aggdesigner-algorithm -Dversion=5.1.5-jhyde -Dpackaging=jar -Dfile=C:/users/49921/Desktop/pentaho-aggdesigner-algorithm-5.15-jhyde.jar

   ###### mvn install:install-file将本地一个中央仓库没有的jar包，推到本地仓库

   ###### 参考链接 ： https://www.cnblogs.com/daofree/p/12681855.html

   

#### 2. window环境下注释掉 hudi-integ-test和hudi-integ-test-bundle

```shell
# 报错信息
[ERROR] Failed to execute goal org.codehaus.mojo:exec-maven-plugin:1.6.0:exec (Setup HUDI_WS) on project hudi-integ-test: Command execution failed.: Cannot run program "\bin\bash" (in directory "C:\Users\49921\Desktop\hudi-release-0.8.0\hudi-release-0.8.0\hudi-integ-test"): CreateProcess error=2, 系统找不到指定的文件。 -> [Help 1]
```

**解决办法**

POM 文件注释报错的相关模块

```xml
 <modules>
    <module>hudi-common</module>
    <module>hudi-cli</module>
    <module>hudi-client</module>
    <module>hudi-hadoop-mr</module>
    <module>hudi-spark-datasource</module>
    <module>hudi-timeline-service</module>
    <module>hudi-utilities</module>
    <module>hudi-sync</module>
    <module>packaging/hudi-hadoop-mr-bundle</module>
    <module>packaging/hudi-hive-sync-bundle</module>
    <module>packaging/hudi-spark-bundle</module>
    <module>packaging/hudi-presto-bundle</module>
    <module>packaging/hudi-utilities-bundle</module>
    <module>packaging/hudi-timeline-server-bundle</module>
    <module>docker/hoodie/hadoop</module>
    <!--  如下两个test模块注释掉
	<module>hudi-integ-test</module>
    <module>packaging/hudi-integ-test-bundle</module>
	-->
    <module>hudi-examples</module>
    <module>hudi-flink</module>
    <module>packaging/hudi-flink-bundle</module>
  </modules>
```

#### 3. Failure to find org.glassfish:javax.el:pom:3.0.1-b06-SNAPSHOT in 

**解决办法**

找到本地maven仓库 ： C:\Users\49921\.m2\repository\org\glassfish\javax.el\3.0.1-b11-SNAPSHOT

把pom.lastupdate文件名修改成.pom ， 每一个版本下的SNAPSHOT名字都要改，然后idea 重新导包

## Hudi 集成Hive

1. 将hudi-hadoop-mr-bundle-0.8.0.jar 拷贝到 Hive lib下

```shell
[root@node1 ~]# scp hudi-hadoop-mr-bundle-0.8.0.jar /opt/software/hive-3.1.2/lib/
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
       <!--  Spark 3.0 集成Hudi 0.8 -->
        <scala.version>2.12</scala.version>
        <scala.binary.version>2.12.12</scala.binary.version>
        <spark.version>3.0.0</spark.version>
        <hoodie.version>0.8.0</hoodie.version>
        <hadoop.version>3.1.3</hadoop.version>
        <hive.version>3.1.2</hive.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-common</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.parquet</groupId>
            <artifactId>parquet-hive-bundle</artifactId>
            <version>1.11.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-spark3-bundle_${scala.version}</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-hadoop-mr-bundle</artifactId>
            <version>${hoodie.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.hudi</groupId>
            <artifactId>hudi-hive-sync-bundle</artifactId>
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

