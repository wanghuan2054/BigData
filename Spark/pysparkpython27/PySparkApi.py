# -*- coding: utf-8 -*-

import findspark
findspark.init()
from pyspark.sql import SparkSession
from datetime import datetime, date
import pandas as pd
from pyspark.sql import Row
from pyspark.sql import Column
from pyspark.sql.functions import pandas_udf
from pyspark.sql.functions import expr


# DataFrame Creation
def createDataFrame():
    # create a PySpark DataFrame from a list of rows
    df = spark.createDataFrame([
        Row(a=1, b=2., c='string1', d=date(2000, 1, 1), e=datetime(2000, 1, 1, 12, 0)),
        Row(a=2, b=3., c='string2', d=date(2000, 2, 1), e=datetime(2000, 1, 2, 12, 0)),
        Row(a=4, b=5., c='string3', d=date(2000, 3, 1), e=datetime(2000, 1, 3, 12, 0))
    ])
    # print(df.show())
    # Create a PySpark DataFrame with an explicit schema
    df1 = spark.createDataFrame([
        (1, 2., 'string1', date(2000, 1, 1), datetime(2000, 1, 1, 12, 1)),
        (2, 3., 'string2', date(2000, 2, 1), datetime(2000, 1, 2, 12, 2)),
        (3, 4., 'string3', date(2000, 3, 1), datetime(2000, 1, 3, 12, 3))
    ], schema='a long, b double, c string, d date, e timestamp')

    # print(df1.show())
    # Create a PySpark DataFrame from a pandas DataFrame
    pandas_df = pd.DataFrame({
        'a': [1, 2, 3],
        'b': [2., 3., 4.],
        'c': ['string1', 'string2', 'string3'],
        'd': [date(2000, 1, 1), date(2000, 2, 1), date(2000, 3, 1)],
        'e': [datetime(2000, 1, 1, 12, 0, 2), datetime(2000, 1, 2, 12, 0, 24), datetime(2000, 1, 3, 12, 0, 14)]
    })
    df2 = spark.createDataFrame(pandas_df)
    # print(df2.show())

    # Create a PySpark DataFrame from an RDD consisting of a list of tuples
    rdd = spark.sparkContext.parallelize([
        (1, 2., 'string1', date(2000, 1, 1), datetime(2000, 1, 1, 12, 0)),
        (2, 3., 'string2', date(2000, 2, 1), datetime(2000, 1, 2, 12, 0)),
        (3, 4., 'string3', date(2000, 3, 1), datetime(2000, 1, 3, 12, 0))
    ])
    df3 = spark.createDataFrame(rdd, schema=['a', 'b', 'c', 'd', 'e'])

    return df3

# viewing the data
def viewingData(df) :
    spark.conf.set('spark.sql.repl.eagerEval.enabled', True)
    # print(df.show())
    # print(df.printSchema())
    # print(df.show(2))
    # print(df.columns)

    # 列垂直显示
    # print(df.show(1, vertical=True))
    # 列汇总信息显示
    # print(df.select("a", "b", "c").describe().show())

    # collects all the data from executors to the driver side ， 如果数据集过大会造成OOM
    # print(df.collect())

    # 拉取固定条数的数据 , 避免OOM
    # print(df.take(1))
    # print(df.tail(1))
    # 拉取df所有data 生成dataframe ， 会造成OOM
    print(df.toPandas())

# Selecting and Accessing Data
def selectData(df) :
    # 返回某一列的实例 ， 延迟计算并不会返回数据
    # print(df.a)
    # print(type(df.c) == type(upper(df.c)) == type(df.c.isNull()))

    # 选择某一列
    # print(df.select(df.c).show())

    # 追加一列
    #print(df.withColumn('upper_c', upper(df.c)).show())
    # 过滤行
    print(df.filter(df.c == 'string2').show())

# Grouping Data
def plus_mean(pandas_df):
    return pandas_df.assign(v1=pandas_df.v1 - pandas_df.v1.mean())
def groupData() :
    df = spark.createDataFrame([
        ['red', 'banana', 1, 10], ['blue', 'banana', 2, 20], ['red', 'carrot', 3, 30],
        ['blue', 'grape', 4, 40], ['red', 'carrot', 5, 50], ['black', 'carrot', 6, 60],
        ['red', 'banana', 7, 70], ['red', 'grape', 8, 80]], schema=['color', 'fruit', 'v1', 'v2'])
    # print(df.show())

    # print(df.groupby('color').avg().show())
    # print(df.groupby('fruit').max('v1').show())
    print(df.groupby('color').applyInPandas(plus_mean, schema=df.schema).show())

# Co-grouping and applying a function
def asof_join(l, r):
    return pd.merge_asof(l, r, on='time', by='id')

def co_groupData() :
    df1 = spark.createDataFrame(
        [(20000101, 1, 1.0), (20000101, 2, 2.0), (20000102, 1, 3.0), (20000102, 2, 4.0)],
        ('time', 'id', 'v1'))

    df2 = spark.createDataFrame(
        [(20000101, 1, 'x'), (20000101, 2, 'y')],
        ('time', 'id', 'v2'))
    print(df1.show())
    print(df2.show())
    print(df1.groupby('id').cogroup(df2.groupby('id')).applyInPandas(
        asof_join, schema='time int, id int, v1 double, v2 string').show())

# Getting Data in/out
def csvImExport() :
    df.write.csv('foo', header=True)
    spark.read.csv('foo', header=True).show()

def parquetImExport() :
    df.write.parquet('bar.parquet')
    spark.read.parquet('bar.parquet').show()
def orcImExport() :
    df.write.orc('zoo.orc')
    spark.read.orc('zoo.orc').show()

# Working with SQL
def add_one(s):
    return s + 1
def sparkSQL():
    df.createOrReplaceTempView("tableA")
    spark.udf.register("add_one", add_one)
    # print(spark.sql("SELECT * from tableA").show())
    # print(spark.sql("SELECT count(*) from tableA").show())
    print(spark.sql("SELECT add_one(a) as a_plus_1 , * FROM tableA").show())
    # print(df.selectExpr('add_one(a)').alias('a_plus_1').show())
    # print(df.select(expr('count(*)') > 0).alias('CNT').show())
if __name__ == '__main__':
    spark = SparkSession.builder.getOrCreate()
    # 生成 DataFrame
    df = createDataFrame()
    df1 = df.alias('df1')
    df2 = df.alias('df2')
    cols = ['a', 'b', 'c', 'd', 'e']
    # df.a, df.b, df.c, df.d, df.e
    print df.select(df.a, df.b, (df.a+df.b).alias('TTL'), df.c, df.d, df.e).show()
    condition = [df1.a == df2.a]
    print df1
    df1 = df1.join(df2,condition,'cross').select(df1.a, df1.b, (df1.a+df2.b).alias('TT12L'), df1.c, df1.d, df2.e).show()
    print df1
    # 显示DataFrame
    # viewingData(df)
    # selectData(df)
    # groupData()
    # co_groupData()
    # csvImExport()
    # parquetImExport()
    # orcImExport()
    # sparkSQL()