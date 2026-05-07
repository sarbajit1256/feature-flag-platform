/*
package com.featureflag.system.kafka;

import com.featureflag.system.dto.FeatureEvent;
import com.featureflag.system.model.FeatureEventEntity;
import com.featureflag.system.repository.FeatureEventRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final FeatureEventRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public KafkaConsumerService(FeatureEventRepository repository,
                                RedisTemplate<String, Object> redisTemplate) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = "feature-events", groupId = "feature-group")
    public void consume(FeatureEvent event) {

        System.out.println("📥 Consumed event: " + event);

        // =========================
        // 1️⃣ Store in DB (history)
        // =========================
        FeatureEventEntity entity = new FeatureEventEntity(
                null,
                event.getUserId(),
                event.getFlagKey(),
                event.getVariant(),
                event.getTimestamp()
        );

        repository.save(entity);

        // =========================
        // 2️⃣ Redis REAL-TIME COUNTER
        // =========================
        String countKey = "flag:" + event.getFlagKey() + ":" + event.getVariant();

        redisTemplate.opsForValue().increment(countKey);

        // =========================
        // 3️⃣ UNIQUE USERS TRACKING
        // =========================
        String userSetKey = "flag:" + event.getFlagKey() + ":users";

        redisTemplate.opsForSet().add(userSetKey, event.getUserId());

        // =========================
        // 4️⃣ DEBUG LOGS
        // =========================
        System.out.println("📊 Updated Count Key: " + countKey);
        System.out.println("👤 Added user to set: " + userSetKey);
    }
}
    */

package com.featureflag.system.kafka;

import com.featureflag.system.dto.FeatureEvent;
import com.featureflag.system.model.FeatureEventEntity;
import com.featureflag.system.repository.FeatureEventRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class KafkaConsumerService {

    private final FeatureEventRepository repository;
    private final RedisTemplate<String, Object> redisTemplate;

    public KafkaConsumerService(
            FeatureEventRepository repository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(
            topics = "feature-events",
            groupId = "feature-group"
    )
    public void consume(FeatureEvent event) {

        System.out.println("📥 Consumed Event: " + event);

        // ✅ UNIQUE EVENT KEY
        String uniqueEventKey =
                "event:" +
                event.getUserId() + ":" +
                event.getFlagKey();

        // ✅ Consumer-side idempotency
        Boolean alreadyProcessed =
                redisTemplate.hasKey(uniqueEventKey);

        if (Boolean.TRUE.equals(alreadyProcessed)) {

            System.out.println(
                    "⚠ Duplicate exposure ignored: "
                    + uniqueEventKey
            );

            return;
        }

        // ✅ Mark processed
        redisTemplate.opsForValue()
                .set(
                        uniqueEventKey,
                        "PROCESSED",
                        Duration.ofDays(30)
                );

        // =========================
        // 1️⃣ Store in DB
        // =========================
        FeatureEventEntity entity =
                new FeatureEventEntity(
                        null,
                        event.getUserId(),
                        event.getFlagKey(),
                        event.getVariant(),
                        event.getTimestamp()
                );

        repository.save(entity);

        // =========================
        // 2️⃣ REAL-TIME VARIANT COUNTER
        // =========================
        String countKey =
                "flag:" +
                event.getFlagKey() +
                ":" +
                event.getVariant();

        redisTemplate.opsForValue()
                .increment(countKey);

        // =========================
        // 3️⃣ UNIQUE USER TRACKING
        // =========================
        String userSetKey =
                "flag:" +
                event.getFlagKey() +
                ":users";

        redisTemplate.opsForSet()
                .add(userSetKey, event.getUserId());

        // =========================
        // 4️⃣ DEBUG LOGS
        // =========================
        System.out.println(
                "📊 Variant Count Updated: "
                + countKey
        );

        System.out.println(
                "👤 User Added To Set: "
                + userSetKey
        );

        System.out.println(
                "✅ Exposure Persisted Successfully"
        );
    }
}