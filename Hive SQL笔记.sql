
-- 创建数据库 ， 默认位置为/user/hive/warehouse/
 create database BOE ;
 
-- 创建数据表，默认位置为/user/hive/warehouse/dbname/tablename
-- 内部表数据存放在指定数据库表的文件夹下面
-- 创建内部表，不指定分隔符 
		Create table IF NOT EXISTS t1(id int , name string , age int ); 

		-- 创建内部表，指定分隔符 
		Create table IF NOT EXISTS t2(id int , name string , age int ) ROW FORMAT delimited fields terminated by ','; 

		-- 复杂类型，指定分割符
		数据如下：
		zhangsan	shanghai,beijing,tianjin,wuhan
		wangwu	guangzhou,chengdu,chongqing,sichuan

		Create table IF NOT EXISTS array_complex(name string , work_locations ARRAY<string>) 
		ROW FORMAT delimited fields terminated by '\t'
		COLLECTION ITEMS TERMINATED BY ','; 

		select * from array_complex ;

		数据如下：
		1,王欢,唱歌:不喜欢-打篮球:钟爱-游泳:擅长
		2,范欣桐,唱歌:非常喜欢-跳舞:喜欢-游泳:一般般

		Create table IF NOT EXISTS hobby_map(id int , name string , map_hobby MAP<string,string>) 
		ROW FORMAT delimited fields terminated by ','
		COLLECTION ITEMS TERMINATED BY '-'
		map keys TERMINATED BY ':'; 

		select *from hobby_map ;


		-- 创建表，使用默认分割符 '\001' ^A
		vi 编辑器中先ctrl+v , 后 ctrl+A
		Create table IF NOT EXISTS t1(id int , name string); 

		select * from t1 ;

		-- 当我们的数据格式比较特殊时候，可以自定义serde（序列化反序列化）
		
-- 创建外部表 ,使用location指定数据文件路径，进行关联
-- 外部表在默认位置 /user/hive/warehouse/ 下没有目录	
		Create external table IF NOT EXISTS external_stu(id int , name string,age int) ROW FORMAT delimited fields terminated by ',' location '/hivedata';

-- 内部表、外部表区别：
   创建内部表时，会将数据移动到数据仓库指向的位置，创建外部表时，仅记录数据所在的路径，不会对数据的位置做
      任何改变。在删除表的时候，内部表的元数据和数据会被一起删除，而外部表只删除元数据，不删除数据。

-- like 允许用户复制现有的表结构，但是不复制数据
    create table t1_copy like t1 ;
    select * from t1_copy ;

-- 单分区表创建 , 分区表的字段不能在表中存在 （静态分区）
		Create table IF NOT EXISTS day_table(name string ,id int)
		PARTITIONED BY (country string)
		ROW FORMAT delimited fields terminated by ',';

		desc formatted day_table;

		LOAD DATA LOCAL INPATH '/opt/data/name.txt' into table  day_table partition(country='USA');

		LOAD DATA LOCAL INPATH '/opt/data/name.txt' into table  day_table partition(country='CHINA');

		--按照分区查询
		select * from day_table where country = 'CHINA' ;
		--查看分区情况
		show partitions day_table;


-- 双分区表创建 , 分区表的字段不能在表中存在（分区名）
		Create table IF NOT EXISTS day_hour_table(name string ,id int)
		PARTITIONED BY (day string,hour string)
		ROW FORMAT delimited fields terminated by ',';

		desc formatted day_table;

		LOAD DATA LOCAL INPATH '/opt/data/name.txt' into table  day_hour_table partition(day='20200221',hour='10');

		LOAD DATA LOCAL INPATH '/opt/data/name.txt' into table  day_hour_table partition(day='20200221',hour='11');

		--双分区查询
		select *from day_hour_table where day=20200221 and hour = '10' ;

-- 动态分区，不需要导入数据时手动指定分区
   --是否开启动态分区功能，默认是关闭的
   set hive.exec.dynamic.partition=true;
   -- 动态分区的模式，默认是strict，表示必须指定至少一个分区为静态分区
   -- nonstrict 模式表示允许所有的分区字段都可以使用动态分区
   set hive.exec.dynamic.partition.mode=nonstrict;
   
   --将一个dynamic_partition_table表中数据， 按照某些字段动态插入到分区表中
    Create table IF NOT EXISTS dynamic_partition_table(day string ,salary string) ROW FORMAT delimited fields terminated by ',';
    
数据如下：
2020-02-01,1000
2020-02-05,1000
2020-02-03,1000
2020-02-12,1000
2020-02-05,1000
2020-02-20,1000
    
    -- 加载数据到dynamic_partition_table表中
    LOAD DATA LOCAL INPATH '/opt/data/dynamic_partition_table.txt' /user/hive/warehouse/dynamic_partition_table；
    
    -- 创建分区表
    Create table IF NOT EXISTS d_part_table(salary string) PARTITIONED BY (month string,day string);
    
    -- 加载数据并动态指定分区
    insert overwrite table d_part_table PARTITION(month,day) select salary ,substr(day,1,7) as month , day from dynamic_partition_table;
    
    -- 动态分区是通过位置来对应分区字段值的，原始表select出来的值和输出partition的值的关系仅仅是通过位置来确定的，和名字没有关系
    --例如上述month和partition中month对应不是因为名字一样，而是应为位置顺序一致。

-- 分桶表
		-- 默认hive 的分桶功能没有开启
		set hive.enforce.bucketing;

		--开启分桶
		set hive.enforce.bucketing=true;
		--设置桶个数，即是reducer个数
		set mapreduce.job.reduces =4  ;

		--分桶表的数据导入不能使用load方式 ， 分桶字段必定来自于表结构字段中
		--load原理是hive使用bin/hdfs dfs -put命令上传文件
		Create table IF NOT EXISTS stu_bucket(name string ,id int)
		clustered BY (name)
		into 4 buckets
		ROW FORMAT delimited fields terminated by ',';

		-- 创建临时表，作为导入桶表的数据源
		Create table IF NOT EXISTS tmp_stu(name string ,id int)
		ROW FORMAT delimited fields terminated by ',';

		--load数据到临时表中
		LOAD DATA LOCAL INPATH '/opt/data/name.txt' into table  tmp_stu;

		-- 借助临时表，导入数据到桶表，select需要执行mapreduce程序 ，对应mr中的partitioner
		insert overwrite table stu_bucket select *from tmp_stu cluster by(name);

		--查询桶表
		select *from stu_bucket;
		--分桶的好处
		1. 获得更高的查询效率，大表对大表，对表中相同的列进行分桶，join时只对保存了相应列值的桶进行join即可，大大减少了join的数据量
		2. 使取样更高效，


-- DDL操作
-- 修改表
   增加分区：
   ALTER TABLE day_table ADD PARTITION(Country='JAPAN') location '/user/hive/warehouse/day_table/country=JAPAN';
   ALTER TABLE day_table ADD PARTITION(Country='KOREA'); -- 不指定分区命名路径，保存在默认路径下
   一次增加多个分区：
   ALTER TABLE day_table ADD PARTITION(Country='ASIA') location '/user/hive/warehouse/day_table/country=ASIA';
   ALTER TABLE day_table ADD PARTITION(Country='EUR') location '/user/hive/warehouse/day_table/country=EUR';
	 
	 删除分区：
	 ALTER TABLE day_table DROP IF EXISTS PARTITION(Country='EUR');
	 
	 修改分区：
	 ALTER TABLE day_table PARTITION(Country='JAPAN') RENAME TO  PARTITION(Country='JJ');
	 
--显示命令
  show tables;
  显示当前数据库所有表
  show databases | schemas;
  显示所有数据库
  show partitions table_name;
  显示表分区信息，不是分区表执行报错
  show functions ;
  显示当前hive版本支持的所有函数
  desc extended table_name;
  查看表信息
  desc formatted table_name;
  查看表信息（格式化美观）
  describe database database_name;
  查看数据库相关信息
  
  
-- Load 
   将数据加载到表中，hive不会进行任何转换，加载操作就是将数据文件移动到hdfs路径下
   LOAD  DATA LOCAL INPATH ， Local是指hive服务运行的主机本地文件系统。如果使用beeline客户端连接，local是指HiveServer2所在服务器路径
   
--Insert 多重插入
  将第一张表的数据查询出，插入下面两张表
  CREATE TABLE IF NOT EXISTS 	tmp_user(name string ,id int)
		ROW FORMAT delimited fields terminated by ',';
	
	CREATE TABLE IF NOT EXISTS Name(name string) ROW FORMAT delimited fields terminated by ',';
	
	CREATE TABLE IF NOT EXISTS ID(id int) ROW FORMAT delimited fields terminated by ',';
	
	-- 执行mr程序
	FROM tmp_user insert overwrite table Name select name insert overwrite table ID select id;
	 
	 
--导出数据
   -- 导出表数据到本地文件系统，并指定输出文件分隔符
   insert overwrite local directory '/opt/data/hiveexport' row format delimited fields terminated by ',' select * from test;
    
   -- 导出表数据到hdfs文件系统，hive 0.13版本之后可以指定输出文件分隔符，之前版本不支持指定输出分隔符
   -- Bug 若是hive0.13及以前版本，会报错
   insert overwrite directory '/hivedata' row format delimited fields terminated by ','  select * from test;
   
   
-- 排序
   1. order by  对全局数据进行排序，因此启动一个reducer，当输入数据较大时，需要较长的计算时间
   2. sort by 不是全局排序，在数据进入reducer之前进行排序。因此使用sort by  进行排序，并且设置mapreduce.job.reduces > 1,则sort by 只能保证每个reducer的输出有序，不保证全局有序
   3. distribute by(字段)，根据字段将数据分到不同的reducer ， 分发算法是hash散列
   
   -- cluster by 分桶查询，并且对name排序 默认是升序
   select * from stu_bucket cluster by(name) ; 
   
   -- 如果distribute 和sort 的是同一个字段时
   cluster by() =  distribute by + sort by 

-- Hive 仿照oracle ，创建一个dual表，用于测试函数
   create table dual(id string) ;
   load data local inpath '/opt/data/dual.txt' into table dual;
   
   -- 函数测试不走mr，需要hive-site中设置hive.fetch.task.conversion 为more ，重启HiveServer2
   select substr('wanghuan',2,3) from dual;
   
   
   create temporary function lowercase as 'com.boe.HiveUDF';
   
   create function udffunc as 'com.boe.HiveUDF' using jar 'hdfs://cdh/user/hive/udflib/HiveUDF.jar';
   
   
-- 特殊分隔符导入处理
   1||wanghuan
   2||huanhuan
   3||fanxintong
   
   create table if not exists t_spchar(id int ,name string) ROW FORMAT delimited fields terminated by '||';
   
    --load数据到表中
		LOAD DATA LOCAL INPATH '/opt/data/spchar.txt' into table  t_spchar;
		
		--默认的delimited fields 为char ， 单字符，不能对多字符进行分割，即LazySimpleSerDe类
		select *  from t_spchar ;
		
		
		-- 使用正则表达式分割字符
		create table if not exists t_regexchar(id int ,name string) ROW FORMAT SERDE   'org.apache.hadoop.hive.serde2.RegexSerDe' with SERDEPROPERTIES('input.regex'='(.*)\\|\\|(.*)','output.format.string'='%1$s %2$s') STORED AS TEXTFILE ;
		
		LOAD DATA LOCAL INPATH '/opt/data/spchar.txt' into table  t_regexchar;
		
		详细用法:https://cwiki.apache.org/confluence/display/Hive/LanguageManual+DDL#LanguageManualDDL-RowFormat,StorageFormat,andSerDe
		
		
		