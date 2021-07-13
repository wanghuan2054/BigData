# -*- coding: utf-8 -*-

import findspark
findspark.init()
from pyspark.sql import SparkSession
import pandas as pd
from pyspark.sql.functions import split , explode , trim

pd.set_option('display.max_columns', 1000)
pd.set_option('display.width', 1000)
pd.set_option('display.max_colwidth', 1000)


# DataFrame Creation
def createDataFrame():
    pandas_df = pd.DataFrame({
        'data': ['string1 af we    we', 'string2  af we    we', 'string3  af we we    ']
    })
    df2 = spark.createDataFrame(pandas_df)
    return df2

# dataframe列数据的拆分
def demo1() :
    separator = ' '
    df = createDataFrame()
    df_split = df.withColumn('split', split(df.data, separator))
    first_row = df.first()
    numAttrs = len(first_row['data'].split(separator))
    attrs = spark.sparkContext.parallelize(["col_" + str(i) for i in range(numAttrs)]).zipWithIndex().collect()
    print attrs
    for name, index in attrs:
        df_split = df_split.withColumn(name, trim(df_split['split'].getItem(index)))
    print df_split.show()

# dataframe将一行分成多行
def demo2() :
    df = createDataFrame()
    print df.show()
    df_explode = df.withColumn("e", explode(split(df['data'], " ")))
    print df_explode.show()

if __name__ == '__main__':
    spark = SparkSession.builder.getOrCreate()
    demo1()