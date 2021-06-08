package sugon.commoncase

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args: Array[String]): Unit = {
    //创建sparkconf运行配置环境
    val sparkConf:SparkConf = new SparkConf().setMaster("local").setAppName("WordCount")
    // 创建spark 上下文环境
    val sparkContext:SparkContext = new SparkContext(sparkConf)
    // 读取数据文件
    val fileRDD:RDD[String] = sparkContext.textFile("input/word.txt")
    // 对文件中的数据进行分词
    val wordRDD:RDD[String] = fileRDD.flatMap(_.split(" "))
    // 转换数据word 到元组  (word,1)
    val word2OneRDD:RDD[(String,Int)] = wordRDD.map((_, 1))
    //  将转换结构后的数据按照相同的单词进行分组聚合
    val word2CountRDD:RDD[(String,Int)] = word2OneRDD.reduceByKey(_ + _)
    // 将数据聚合结果采集到内存中
    val word2Count:Array[(String,Int)] = word2CountRDD.collect()
    // 打印结果
    word2Count.foreach(println)
    //关闭 Spark 连接
    sparkContext.stop()
  }
}
