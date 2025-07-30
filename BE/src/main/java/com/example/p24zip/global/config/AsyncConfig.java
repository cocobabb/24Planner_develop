package com.example.p24zip.global.config;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

    /***
     순간적인 부하 대응
     */
    @Bean(name = "customExecutor")
    public Executor customExecutor() {

        // 커스텀 스레드 생성기
        ThreadFactory customFactory = runnable -> {
            Thread t = new Thread(runnable);
            t.setName("my-custom-thread-" + UUID.randomUUID());
            return t;
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            10,       // corePoolSize: 기본 스레드 수
            20,                // maximumPoolSize: 최대 스레드 수
            60L,              // keepAliveTime: 유휴(idele,할 일이 없이 대기 중인 상태) 스레드 유지 시간
//            1. 작업이 끝나면 해당 스레드는 즉시 종료되지 않고 60초 동안 유휴 상태로 대기
//            2. 그 60초 안에 새로운 작업이 들어오면 → 기존 유휴 스레드를 재사용
//            3. 60초 동안 아무 일도 없으면 → 스레드는 자동 종료
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), // 최대 100개 작업 대기
//            Executors.defaultThreadFactory(), // 기본 스레드 생성기(새로운 스레드를 생성하는 방법을 캡슐화한 인터페이스)
            customFactory,
            new ThreadPoolExecutor.CallerRunsPolicy() // queue 넘치면 메인 스레드에서 실행
        );

        return executor;
    }
}
