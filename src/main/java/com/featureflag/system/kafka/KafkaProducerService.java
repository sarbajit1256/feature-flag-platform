/*
package com.featureflag.system.kafka;

import com.featureflag.system.dto.FeatureEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, FeatureEvent> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, FeatureEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(FeatureEvent event) {
        kafkaTemplate.send("feature-events", event);
        System.out.println("Kafka Event Sent: " + event);
    }
}*/

package com.featureflag.system.kafka;

import com.featureflag.system.dto.FeatureEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "feature-events";

    private final KafkaTemplate<String, FeatureEvent> kafkaTemplate;

    public KafkaProducerService(
            KafkaTemplate<String, FeatureEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(FeatureEvent event) {

        // ✅ Use userId as Kafka key
        // Ensures same user events go to same partition
        String key = event.getUserId();

        CompletableFuture<SendResult<String, FeatureEvent>> future =
                kafkaTemplate.send(TOPIC, key, event);

        // ✅ Async Success
        future.thenAccept(result -> {

            System.out.println(
                    "Kafka Event Sent Successfully -> " +
                    "Topic: " + result.getRecordMetadata().topic() +
                    ", Partition: " + result.getRecordMetadata().partition() +
                    ", Offset: " + result.getRecordMetadata().offset() +
                    ", User: " + event.getUserId() +
                    ", Variant: " + event.getVariant()
            );

        });

        // ✅ Async Failure
        future.exceptionally(ex -> {

            System.err.println(
                    "Kafka Send Failed -> " +
                    ex.getMessage()
            );

            return null;
        });
    }
}