from pyspark import SparkContext, SparkConf


if __name__ == '__main__' :
    conf = SparkConf().setAppName("RDD").setMaster("local[*]")
    sc = SparkContext(conf=conf)