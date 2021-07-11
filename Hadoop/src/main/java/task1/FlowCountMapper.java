package task1;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * LongWritable, Text ===> Map输入    <偏移量，手机号>
 * Text, FlowBean  ======> Map的输出：<手机号、流量上传下载总和>
 */
public class FlowCountMapper extends Mapper<LongWritable, Text, Text, FlowBean> {
    Text k = new Text();
    FlowBean v = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //获取每一行数据
        String line = value.toString();

        //切割字段
        //1363157995052 	13826544101	5C-0E-8B-C7-F1-E0:CMCC	120.197.40.4			4	0	264	0	200
        String[] fields = line.split("\t");
        //手机号
        String phoneNum = fields[1];

        //上传和下载 upFlow downFlow
        long upFlow = Long.parseLong(fields[fields.length - 3]);
        long downFlow = Long.parseLong(fields[fields.length - 2]);

        k.set(phoneNum);

        context.write(k,new FlowBean(upFlow,downFlow));
    }
}