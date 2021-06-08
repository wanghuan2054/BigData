from pyspark import SparkConf, SparkContext


if __name__ == '__main__':
    conf = SparkConf().setAppName("waordcount").setMaster("local[2]")
    sc = SparkContext(conf=conf)
    sc.setLogLevel("ERROR")
    inputdata = sc.textFile("../data/word.txt")
    output = inputdata.flatMap(lambda x: x.split(" ")).map(lambda x: (x, 1)).reduceByKey(lambda a, b: a + b)

    result = output.collect()
    for i in result:
        print(i)

    sc.stop()