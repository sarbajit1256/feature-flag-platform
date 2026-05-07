package com.featureflag.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableKafka   // 🔥 ADD THIS
@EnableScheduling
public class FeatureFlagTestingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeatureFlagTestingPlatformApplication.class, args);
	}

}
