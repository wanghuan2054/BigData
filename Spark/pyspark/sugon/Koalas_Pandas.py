
import pandas as pd
import numpy as np
import databricks.koalas as ks
from pyspark.sql import SparkSession
import warnings

# 初始化demo
def initialDemo() :
    df = ks.DataFrame({'x': [1, 2], 'y': [3, 4], 'z': [5, 6]})
    print(df)
    # Rename columns
    df.columns = ['x', 'y', 'z1']
    print(df)
    # Do some operations in place
    df['x2'] = df.x * df.x
    print(df)

# 对象的创建
def objectCreate() :
    # 通过传递值列表来创建Koalas系列，让Koalas创建默认的整数索引：
    # s = ks.Series([1, 3, 5, np.nan, 6, 8])
    # print(s)

    # 通过传递对象的字典来创建Koalas DataFrame，这些对象可以转换为类似序列的对象
    # kdf = ks.DataFrame(
    #     {'a': [1, 2, 3, 4, 5, 6],
    #      'b': [100, 200, 300, 400, 500, 600],
    #      'c': ["one", "two", "three", "four", "five", "six"]},
    #     index=[10, 20, 30, 40, 50, 60])
    # print(kdf)

    # 通过传递带有日期时间索引和带标签的列的numpy数组来创建pandas DataFrame
    dates = pd.date_range('20210101', periods=6)
    pdf = pd.DataFrame(np.random.randn(6, 4), index=dates, columns=list('ABCD'))
    # print(pdf)

    # Pandas DataFrame 可以转换为Koalas DataFrame
    kdf = ks.from_pandas(pdf)
    print(kdf)
    # 从pandas DataFrame创建Spark DataFrame
    spark = SparkSession.builder.getOrCreate()
    sdf = spark.createDataFrame(pdf)
    sdf.show()

    # 从Spark DataFrame创建Koalas DataFrame。to_koalas()会自动附加到Spark DataFrame，并且在导入Koalas时可用作API
    kdf = sdf.to_koalas()
    print(kdf)

# Data Display | Show
def displayData() :
    dates = pd.date_range('20210101', periods=6)
    # pdf = pd.DataFrame(np.random.randn(6, 4), index=dates, columns=list('ABCD'))
    pdf = pd.DataFrame(np.random.randn(6, 4), columns=list('ABCD'))
    kdf = ks.from_pandas(pdf)
    # print(kdf.head())
    # print(kdf.index)
    # print(kdf.columns)
    # print(kdf.to_numpy())

    # 描述显示您的数据的快速统计摘要
    # print(kdf.describe())

    # 数据转置
    # print(kdf.T)

    # 按其索引排序
    kdf.sort_index(ascending=False)
    print(kdf)
    # 按值排序
    kdf.sort_values(by='A')
    print(kdf)

# Data Process 去重  null值填充
def processData() :
    dates = pd.date_range('20210101', periods=6)
    pdf = pd.DataFrame(np.random.randn(6, 4), index=dates, columns=list('ABCD'))
    kdf = ks.from_pandas(pdf)

    # 取前四行，列表新增一列：E列
    pdf1 = pdf.reindex(index=dates[0:4], columns=list(pdf.columns) + ['E'])
    # print(pdf1)

    # 给索引的第一第二行的E列赋值,因为第三第四行的E列的数据没赋值，所以为空
    pdf1.loc[dates[0]:dates[1], 'E'] = 1
    # print(pdf1)

    # 将此pandas数据框转化为Koalas数据框
    kdf1 = ks.from_pandas(pdf1)
    print(kdf1)

    # 删除任何缺少数据的行
    # kdf1 = kdf1.dropna(how='any')
    # print(kdf1)

    # 填充数值给那些含有空值的数据
    kdf1 = kdf1.fillna(value=24)
    print(kdf1)
    # 查看koalas数据框中数值列的均值，此操作通常会排除丢失的数据。执行描述性统计
    print(kdf1.mean())

# 分组API
def groupBy() :
    kdf = ks.DataFrame({'A': ['foo', 'bar', 'foo', 'bar',
                              'foo', 'bar', 'foo', 'foo'],
                        'B': ['one', 'one', 'two', 'three',
                              'two', 'two', 'one', 'three'],
                        'C': np.random.randn(8),
                        'D': np.random.randn(8)})
    print(kdf)

    # 单字段分组，然后将sum（）函数应用于结果组 , 可以看到：C列和D列关于A索引的数值总和
    # kdf = kdf.groupby('A').sum()
    # print(kdf)
    # 多字段分组
    kdf = kdf.groupby(['A', 'B']).sum()
    print(kdf)

# CSV File Import Export
def fileImExport() :
    kdf = ks.DataFrame({'A': ['foo', 'bar', 'foo', 'bar',
                              'foo', 'bar', 'foo', 'foo'],
                        'B': ['one', 'one', 'two', 'three',
                              'two', 'two', 'one', 'three'],
                        'C': np.random.randn(8),
                        'D': np.random.randn(8)})
    # 将此koalas数据框转化为csv文件
    kdf.to_csv('foo.csv')

    # 读取csv文件为koalas的数据框
    kdf1 = ks.read_csv('foo.csv').head(10)
    print(kdf1)

# parquet File Import Export
# Parquet是一种高效和紧凑的文件格式，读取和写入更快
def parquetImExport() :
    kdf = ks.DataFrame({'A': ['foo', 'bar', 'foo', 'bar',
                              'foo', 'bar', 'foo', 'foo'],
                        'B': ['one', 'one', 'two', 'three',
                              'two', 'two', 'one', 'three'],
                        'C': np.random.randn(8),
                        'D': np.random.randn(8)})
    # 将此koalas数据框转化为Parquet文件格式
    kdf.to_parquet('bar.parquet')

    # 读取Parquet文件为koalas的数据框
    kdf1 = ks.read_parquet('bar.parquet').head(10)
    print(kdf1)


if __name__ == '__main__' :
    # 保持默认值
    # prev = SparkSession.conf.get("spark.sql.execution.arrow.enabled")
    # 使用默认索引防止开销。
    ks.set_option("compute.default_index_type", "distributed")
    # 忽略来自Arrow优化的警告。
    warnings.filterwarnings("ignore")
    # objectCreate()
    # displayData()
    # processData()
    # groupBy()
    # fileImExport()
    parquetImExport()