package com.example;

import com.example.p24zip.domain.chat.entity.Chat;
import com.example.p24zip.domain.chat.repository.ChatRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput}) // 평균 실행 시간 측정, 단위 시간당 작업 처리량
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 결과 단위: ms
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS) // 워밍업 3회
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS) // 측정 10회
public class ChatLoadBenchmark_JPA {

    private static ConfigurableApplicationContext context;
    private final Long movingPlanId = 1L;
    private final Long messageId = 683L;
    private final PageRequest pageable = PageRequest.of(0, 49);
    private ChatRepository chatRepository;


    @Setup(Level.Trial)
    public void setup() {
        if (context == null) {
            context = SpringApplication.run(BenchmarkApplication.class);
        }
        chatRepository = context.getBean(ChatRepository.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    @Benchmark
    public List<Chat> loadFromMySQL_previousMessage_QueryMethod() {
        return chatRepository.findByMovingPlan_IdAndIdLessThanOrderByIdDesc(
            movingPlanId, messageId, pageable);
    }

    @Benchmark
    public List<Chat> loadFromMySQL_previousMessage_JPQL() {
        return chatRepository.findChatsBeforeId(movingPlanId, messageId, pageable);
    }
}
