package sugon.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Arrays;
import java.util.Properties;

public class CustomAutoConsumer {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "node1:9092,node2:9092,node3:9092");
        props.put("group.id", "test1");
        // 开启自动提交offset
        props.put("enable.auto.commit", "true");
        // 自动提交offset的时间间隔
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 设置消费的位置 , earliest 最早 , latest 最近
        props.put("auto.offset.reset", "latest");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(props);
        // TopicPartition partition0 = new TopicPartition("jlwang", 1);
        //TopicPartition partition1 = new TopicPartition("jlwang", 0);
        // consumer.assign(Arrays.asList(partition1));
        // consumer.seek(partition1, 491);
        //  consumer.seek(partition0, 495);
        // 指定消费的topic
        consumer.subscribe(Arrays.asList("wanghuan"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partition=%d, offset = %d, key = %s, value = %s%n", record.partition(),record.offset(), record.key(), record.value());
        }
    }
}
