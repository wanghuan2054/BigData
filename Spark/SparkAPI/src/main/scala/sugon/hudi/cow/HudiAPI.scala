package sugon.hudi.cow

import org.apache.hudi.DataSourceReadOptions.{BEGIN_INSTANTTIME_OPT_KEY, END_INSTANTTIME_OPT_KEY, QUERY_TYPE_INCREMENTAL_OPT_VAL, QUERY_TYPE_OPT_KEY}
import org.apache.hudi.DataSourceWriteOptions.{OPERATION_OPT_KEY, PARTITIONPATH_FIELD_OPT_KEY, PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.{col, concat_ws, lit}
import org.apache.spark.sql.{SaveMode, SparkSession}

object HudiAPI {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "root")
    val sparkConf = new SparkConf().setAppName("hudi_data_import")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .setMaster("local[*]")
    val sparkSession = SparkSession.builder().config(sparkConf). /*enableHiveSupport().*/ getOrCreate()
    val ssc = sparkSession.sparkContext
    ssc.hadoopConfiguration.set("fs.defaultFS", "hdfs://myhadoop")
    ssc.hadoopConfiguration.set("dfs.nameservices", "myhadoop")
    // 设置Log Console输出量
    ssc.setLogLevel("ERROR")
        hudiInsertData(sparkSession)
        queryHudiData(sparkSession)
//    hudiUpdateData(sparkSession)
//    queryHudiData(sparkSession)
    //    incrQueryHudiData(sparkSession)  // 增量查询hudi数据
    //     rangeQueryHudiData(sparkSession)
    //    deleteDataByRK(sparkSession)
  }

  // 读取hdfs 文件，写入hudi
  def hudiInsertData(sparkSession: SparkSession): Unit = {
    // 生成提交时间
    val commitTime = System.currentTimeMillis().toString
    // 生成Hudi表的表名
    val tableName = "hudi_cow_table"
    // hudi 表的存储目录
    val hdfsPath = "/hudi/hudi_table"
    // hdfs 路径
    val df = sparkSession.read.json("/tmp/people.json")
    df.createOrReplaceTempView("people")
    val sqlDF = sparkSession.sql("SELECT country , city , id , name , age FROM people")
    val dfResult = sqlDF
      .withColumn("ts", lit(commitTime))
      .withColumn("uuid", col("id")) // 依据uuid 列进行upsert判断
      .withColumn("partitionpath", concat_ws("/", col("country"), col("city"))) // 增加hudi的分区路径字段
    //    print(dfResult.show())

    dfResult.write
      .format("org.apache.hudi") // 设置输出格式为hudi
      // 根据实际vcore 设置，会加快insert 和 upsert 速度
      .option("hoodie.insert.shuffle.parallelism", 8)
      .option("hoodie.upsert.shuffle.parallelism", 8)
      .option(RECORDKEY_FIELD_OPT_KEY, "id") // 设置主键列名
      .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
      .option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath")
      .option(HoodieWriteConfig.TABLE_NAME, tableName)
      .mode(SaveMode.Overwrite)
      .save(hdfsPath)
  }

  // 读取hudi数据
  def queryHudiData(sparkSession: SparkSession): Unit = {
    val df = sparkSession.read.format("hudi")
      .load("/hudi/hudi_table/*/*")
    df.createOrReplaceTempView("hudi_people")
    //    print(sparkSession.sql("select * from hudi_people where country='China'").show())
    print(sparkSession.sql("select * from hudi_people order by id").show())
  }

  // 读取hdfs 文件，写入hudi (upSert验证)
  def hudiUpdateData(sparkSession: SparkSession): Unit = {
    // 生成提交时间
    val commitTime = System.currentTimeMillis().toString
    // 生成Hudi表的表名
    val tableName = "hudi_cow_table"
    // hudi 表的存储目录
    val hdfsPath = "/hudi/hudi_table"
    // hdfs 路径
    val df = sparkSession.read.json("/tmp/newpeople.json")
    df.createOrReplaceTempView("people")
    val sqlDF = sparkSession.sql("SELECT country , city , id , name , age FROM people")
    println(commitTime)
    println(lit(commitTime))
    val dfResult = sqlDF.withColumn("ts", lit(commitTime))
      .withColumn("uuid", col("id")) // 依据uuid 列进行upsert判断
      .withColumn("partitionpath", concat_ws("/", col("country"), col("city"))) // 增加hudi的分区路径字段
    //    print(dfResult.show())

    dfResult.write
      .format("org.apache.hudi") // 设置输出格式为hudi
      // 根据实际vcore 设置，会加快insert 和 upsert 速度
      .option("hoodie.insert.shuffle.parallelism", 8)
      .option("hoodie.upsert.shuffle.parallelism", 8)
      .option(RECORDKEY_FIELD_OPT_KEY, "uuid") // 设置主键列名
      .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
      .option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath")
      .option(HoodieWriteConfig.TABLE_NAME, tableName)
      .mode(SaveMode.Append)
      .save(hdfsPath)
  }

  // 增量读取hudi数据
  def incrQueryHudiData(sparkSession: SparkSession): Unit = {
    // 生成Hudi表的表名
    val tableName = "hudi_cow_table"
    // hudi 表的存储目录
    val hdfsPath = "/hudi/hudi_table"
    val df = sparkSession.read.format("hudi")
      .load("/hudi/hudi_table/*/*")
      .createOrReplaceTempView("hudi_people")
    //    print(sparkSession.sql("select * from hudi_people where country='China'").show())
    implicit val encoder = org.apache.spark.sql.Encoders.STRING
    val commits = sparkSession.sql("select distinct(_hoodie_commit_time) as commitTime from  hudi_people order by commitTime")
      .map(s => s.getString(0)).take(10)
    val beginTime = commits(commits.length - 2) // commit time we are interested in
    //    print(beginTime)
    // 增量查询数据
    val incViewDF = sparkSession.
      read.
      format("org.apache.hudi").
      option(QUERY_TYPE_OPT_KEY, QUERY_TYPE_INCREMENTAL_OPT_VAL). // 指定模式为增量查询
      option(BEGIN_INSTANTTIME_OPT_KEY, beginTime).
      load(hdfsPath);
    //    incViewDF.registerTempTable("hudi_incr_table")
    incViewDF.createOrReplaceTempView("hudi_incr_table")
    print(sparkSession.sql("select  * from  hudi_incr_table").show())
  }

  // 特定时间区间范围查询
  def rangeQueryHudiData(sparkSession: SparkSession): Unit = {
    implicit val encoder = org.apache.spark.sql.Encoders.STRING
    // hudi 表的存储目录
    val hdfsPath = "/hudi/hudi_table"
    val df = sparkSession.read.format("hudi")
      .load("/hudi/hudi_table/*/*")
      .createOrReplaceTempView("hudi_people")
    //    print(sparkSession.sql("select * from hudi_people where country='China'").show())

    val commits = sparkSession.sql("select distinct(_hoodie_commit_time) as commitTime from  hudi_people order by commitTime")
      .map(s => s.getString(0)).take(10)
    val beginTime = "000" // commit time we are interested in
    val endTime = commits(commits.length - 1) // commit time we are interested in
    //    print(beginTime)
    // 增量查询数据
    val incViewDF = sparkSession.
      read.
      format("org.apache.hudi").
      option(QUERY_TYPE_OPT_KEY, QUERY_TYPE_INCREMENTAL_OPT_VAL).
      option(BEGIN_INSTANTTIME_OPT_KEY, beginTime).
      option(END_INSTANTTIME_OPT_KEY, endTime).
      load(hdfsPath);
    //    incViewDF.registerTempTable("hudi_incr_table")
    incViewDF.createOrReplaceTempView("hudi_incr_table")
    print(sparkSession.sql("select  * from  hudi_incr_table").show())
  }

  // 删除指定RecordKey的数据
  def deleteDataByRK(sparkSession: SparkSession): Unit = {
    implicit val encoder = org.apache.spark.sql.Encoders.STRING
    // hudi 表的存储目录
    val hdfsPath = "/hudi/hudi_table"
    // 生成Hudi表的表名
    val tableName = "hudi_cow_table"
    //    val df = sparkSession.read.format("hudi")
    //      .load("/hudi/hudi_table/*/*")
    //      .createOrReplaceTempView("hudi_cow_table")
    // 获取记录总数
    //    val cnt = sparkSession.sql("select uuid, partitionpath from hudi_cow_table group by uuid having count(*) > 1 ").count()
    //    print(cnt)
    // 拿到两条将要删除的数据
    val ds = sparkSession.sql("select uuid from hudi_cow_table group by uuid having count(*) > 1").limit(2)
    //    print(ds.show())
    //
    //    // 执行删除
    //    val deletes = dataGen.generateDeletes(ds.collectAsList())

    val df = sparkSession.read.json("/tmp/newpeople.json")
    //    val df = sparkSession.read.json(sparkSession.sparkContext.parallelize(deletes, 2))

    //
    df.write.format("hudi").
      options(getQuickstartWriteConfigs).
      option(OPERATION_OPT_KEY, "delete").
      option(PRECOMBINE_FIELD_OPT_KEY, "ts").
      option(RECORDKEY_FIELD_OPT_KEY, "uuid").
      option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath").
      option(HoodieWriteConfig.TABLE_NAME, tableName).
      mode(SaveMode.Append).
      save(hdfsPath)
    //
    //    // 向之前一样运行查询
    val roAfterDeleteViewDF = sparkSession.
      read.
      format("hudi").
      load(hdfsPath + "/*/*/")
    //
    roAfterDeleteViewDF.registerTempTable("hudi_trips_snapshot")
    roAfterDeleteViewDF.createOrReplaceTempView("hudi_delete_table")
    //    // 应返回 (total - 2) 条记录
    val cnt = sparkSession.sql("select uuid, partitionpath from hudi_delete_table").count()
    print(cnt)
  }
}
