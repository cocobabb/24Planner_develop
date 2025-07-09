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

    /**
     * Spring ApplicationContext가 초기화된 후(서버가 가동된 후) NotificationService의 Redis Pub/Sub 메시지 리스너를 등록하는
     * 메서드
     *
     * @param event ContextRefreshedEvent : Spring이 ApplicationContext를 새로 만들거나 리프레시할 때 발생하는 이벤트 객체
     * @apiNote 이 리스너는 Redis에서 알림 메시지가 오면 해당 유저의 SSE 연결로 실시간 알림을 전송할 수 있게 해줍니다. (즉, 서버가 켜질 때 한 번만
     * 실행되어, 실시간 알림 기능이 동작하도록 초기화합니다)
     */
    @EventListener(ContextRefreshedEvent.class)
    public void setupRedisMessageListener(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        NotificationService notificationService = context.getBean(NotificationService.class);
        notificationService.setupRedisMessageListener();
    }

}
