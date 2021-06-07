package sugon.producer;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class CustomAsyncProducer1 {
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
        // 带回调函数的异步API ， 消息发送失败会自动重试，不需要手动API调用重试函数
        for (int i = 0; i < 100 ; i++) {
            // 异步发送消息
            ProducerRecord<String, String> record = new ProducerRecord<>("wanghuan", "Kafka_Products", Integer.toString(i));//Topic Key Value
            // 该方法在Producer收到ACK返回时， 调用
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {//如果Kafka返回一个错误，onCompletion方法抛出一个non null异常。
                        e.printStackTrace();//对异常进行一些处理，这里只是简单打印出来
                    }else
                        recordMetadata.offset() ;
                }
            });
        }
        producer.close();
    }
}