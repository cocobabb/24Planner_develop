package com.example.p24zip.global.config;


import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

    // 일반적인 비동기 작업용 Executor
    @Bean(name = "customExecutor")
    public Executor customExecutor() {
        ThreadFactory customFactory = runnable -> {
            Thread t = new Thread(runnable);
            t.setName("my-custom-thread-" + UUID.randomUUID());
            return t;
        };

        return new ThreadPoolExecutor(
            3,
            5,
            60L, // 스레드 keepAliveTime 60초
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100), // 최대 100개 작업 대기
            customFactory,
            new ThreadPoolExecutor.AbortPolicy() // 초과 시 예외 발생
        );
    }

    // 메일 타임아웃 감시용 Executor (즉시 실행)
    @Bean(name = "mailTimeoutExecutor")
    public ExecutorService mailTimeoutExecutor() {
        return new ThreadPoolExecutor(
            0,
            30,
            60L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(), // 큐 없이 바로 실행 or reject
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
        );
    }


}
