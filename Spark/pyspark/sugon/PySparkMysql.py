from datetime import datetime

import pymysql
from pyspark import SparkConf, SparkContext
from pyspark.sql import SparkSession
from pyspark.sql import SQLContext


def readMysqlData():
    spark = SparkSession.builder.appName("Pyspark connect mysql") \
        .getOrCreate()
    ctx = SQLContext(spark)
    jdbcDf = spark.read.format("jdbc").options(
        url="jdbc:mysql://192.168.2.7:3306/sugon-hive?characterEncoding=utf-8&autoReconnect=true&useSSL=false",
        driver="com.mysql.jdbc.Driver",
        dbtable="users", user="root",
        password="sugon123").load()
    # print(jdbcDf.printSchema())
    print(jdbcDf.show())
    spark.stop()

def connectMyql():
    # 打开数据库连接
    dbConn = pymysql.connect(host='192.168.2.7', user='root', password='sugon123', db='sugon-hive', port=3306)
    # 使用 cursor() 方法创建一个游标对象 cursor
    cursor = dbConn.cursor()
    try :

        # 使用 execute()  方法执行 SQL 查询
        cursor.execute("SELECT * from users")
        # 使用 fetchone() 方法获取单条数据.
        data = cursor.fetchall()
        for x in data :
            print(x)
    # 关闭数据库连接
    finally:
        dbConn.close()

def insertDataToMysql():
    sc = SparkSession.builder.appName("Pyspark connect mysql") \
        .getOrCreate()
    spark = SQLContext(sc)
    # mysql 配置(需要修改)
    prop = {'user': 'root',
            'password': 'sugon123',
            'driver': 'com.mysql.jdbc.Driver'}
    # database 地址(需要修改)
    url = "jdbc:mysql://192.168.2.7:3306/sugon-hive?characterEncoding=utf-8&autoReconnect=true&useSSL=false"

    # 创建spark DataFrame
    # 方式1：list转spark DataFrame
    l = [(5, 'wang',datetime(2021, 6, 23, 12, 0,8),datetime(2021, 6, 23, 12, 0,9))]
    # 创建并指定列名
    final_df = spark.createDataFrame(l, schema=['id', 'name','birthday', 'ts'])

    # 写入数据库
    final_df.write.jdbc(url=url, table='sugon-hive', mode='append', properties=prop)

    # 关闭spark会话
    spark.stop()

if __name__ == '__main__':
    # 读取mysql数据
    # readMysqlData()
    # 插入mysql数据
    # insertDataToMysql()
    connectMyql()

