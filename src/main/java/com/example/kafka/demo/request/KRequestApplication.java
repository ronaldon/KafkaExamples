package com.example.kafka.demo.request;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;


@SpringBootApplication
public class KRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(KRequestApplication.class, args).close();
    }

    @Bean
    public ApplicationRunner runner(ReplyingKafkaTemplate<String, String, String> template) {
        return args -> {
        	int count = 0;

        	while (count < 2000) {
        		
        		
        		
        		ProducerRecord<String, String> record = new ProducerRecord<>("kRequests",  RandomStringUtils.randomAlphabetic(10).toLowerCase());
                RequestReplyFuture<String, String, String> replyFuture = template.sendAndReceive(record);
                
                SendResult<String, String> sendResult = replyFuture.getSendFuture().get(60, TimeUnit.SECONDS);
                System.out.println("Sent ok: " + sendResult.getRecordMetadata());
                
                ConsumerRecord<String, String> consumerRecord = replyFuture.get(60, TimeUnit.SECONDS);
                System.out.println("Return value: " + consumerRecord.value());
                
                count++;
        	}
            
        };
    }

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingTemplate(
        ProducerFactory<String, String> pf,
        ConcurrentMessageListenerContainer<String, String> repliesContainer) {

        ReplyingKafkaTemplate<String, String, String> kafkaTemplate = new ReplyingKafkaTemplate<>(pf, repliesContainer);
        kafkaTemplate.setSharedReplyTopic(true);
        
		return kafkaTemplate;
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer(
            ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {

        ConcurrentMessageListenerContainer<String, String> repliesContainer =
                containerFactory.createContainer("kReplies");
        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
        repliesContainer.setAutoStartup(true);
        
        return repliesContainer;
    }

    @Bean
    public NewTopic kRequests() {
        return TopicBuilder.name("kRequests")
            .partitions(10)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic kReplies() {
        return TopicBuilder.name("kReplies")
            .partitions(10)
            .replicas(1)
            .build();
    }

}