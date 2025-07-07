package com.example.p24zip;

import com.example.p24zip.domain.movingPlan.service.NotificationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class P24zipApplication {

    public static void main(String[] args) {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(P24zipApplication.class, args);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void setupRedisMessageListener(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        NotificationService notificationService = context.getBean(NotificationService.class);
        notificationService.setupRedisMessageListener();
    }

}
