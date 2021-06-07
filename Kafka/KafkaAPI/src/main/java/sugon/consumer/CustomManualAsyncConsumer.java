package sugon.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

// 手动提交offset
public class CustomManualAsyncConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "node1:9092,node2:9092,node3:9092");
        props.put("group.id", "test1");
        // 关闭自动提交offset
        props.put("enable.auto.commit", "false");
        // 自动提交offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 设置消费的位置 , earliest 最早 , latest 最近
        props.put("auto.offset.reset", "latest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(props);
        // 指定消费的topic , 消费者订阅主题
        consumer.subscribe(Arrays.asList("wanghuan"));
        while (true) {
            // 消费者pull 数据
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("partition=%d, offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), record.value());
            }
            // 异步提交 , offset 提交成功后调用回调函数
            consumer.commitAsync(new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                    if (e!=null){
                        System.out.println("commit failed");
                    }
                }
            });
        }

    }
}
