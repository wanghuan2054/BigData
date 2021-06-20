package sugon.hudi.image

import org.apache.hudi.DataSourceReadOptions.{BEGIN_INSTANTTIME_OPT_KEY, END_INSTANTTIME_OPT_KEY, QUERY_TYPE_INCREMENTAL_OPT_VAL, QUERY_TYPE_OPT_KEY}
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.{col, concat_ws, lit}
import org.apache.spark.sql.{SaveMode, SparkSession}

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HudiImageAPI {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")
    val sparkConf = new SparkConf().setAppName("hudi_image_import")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .setMaster("local[*]")
    val sparkSession = SparkSession.builder().config(sparkConf). /*enableHiveSupport().*/ getOrCreate()
    val ssc = sparkSession.sparkContext
    ssc.hadoopConfiguration.set("fs.defaultFS", "hdfs://myhadoop")
    ssc.hadoopConfiguration.set("dfs.nameservices", "myhadoop")
    // 设置Log Console输出量
    ssc.setLogLevel("ERROR")
    uploadImageToHdfs(sparkSession)
    readImageFromHdfs(sparkSession)
  }

  // 上传图片至hudi
  def uploadImageToHdfs(sparkSession: SparkSession): Unit = {
    // 生成提交时间
    val commitTime = System.currentTimeMillis().toString
    val hdfsPath = "/hudi/image/"
    val tableName = "imageTab"
    val dt = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now())
    val df = sparkSession.read.format("image")
      .option("dropInvalid", "true") // 是否剔除无效图片
      .load("file://" + this.getClass.getResource("/").toURI.getPath)
    //    print(df.printSchema())

    val result = df.select("image.origin", "image.height", "image.width", "image.nChannels", "image.mode",
      "image.data").withColumn("dt", lit(dt))
      .withColumn("ts", lit(commitTime))
      .withColumn("uuid", col("origin"))
    print(result.show())

    result.write
      .format("org.apache.hudi") // 设置输出格式为hudi
      // 根据实际vcore 设置，会加快insert 和 upsert 速度
      .option("hoodie.insert.shuffle.parallelism", 8)
      .option("hoodie.upsert.shuffle.parallelism", 8)
      .option(RECORDKEY_FIELD_OPT_KEY, "uuid") // 设置主键列名
      .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
      .option(PARTITIONPATH_FIELD_OPT_KEY, "dt")
      .option(HoodieWriteConfig.TABLE_NAME, tableName)
      .mode(SaveMode.Overwrite)
      .save(hdfsPath)
  }

  // 读取hudi数据
  def readImageFromHdfs(sparkSession: SparkSession): Unit = {
    val df = sparkSession.read.format("hudi")
      .load("/hudi/image/*/*")
    df.createOrReplaceTempView("hudi_image")
    //    print(sparkSession.sql("select * from hudi_people where country='China'").show())
    print(sparkSession.sql("select * from hudi_image order by uuid").show())
  }
}
