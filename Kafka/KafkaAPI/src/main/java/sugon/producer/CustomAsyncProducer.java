package sugon.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class CustomAsyncProducer {
    public static void main(String[] args) {
        Properties properties = new Properties() ;

        // 获取Kafka集群 的 broker-list
        properties.put("bootstrap.servers","node1:9092") ;
        properties.put("acks","all") ;

        // 重试次数
        properties.put("retries",1) ;

        //批次大小
        properties.put("batch.size",1024) ;

        // 等待时间
        properties.put("linger.ms",1) ;

        // RecordAccumulator 缓冲区大小
        properties.put("buffer.memory",1024) ;

        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String,String> producer =  new KafkaProducer<>(properties) ;
        // 不带回调函数的异步API
        for (int i = 0; i < 100 ; i++) {
            // 异步发送消息 , 不带有回调函数
            producer.send(new ProducerRecord<String , String>("wanghuan",Integer.toString(i),Integer.toString(i))) ;
        }
        producer.close();

    }
}
