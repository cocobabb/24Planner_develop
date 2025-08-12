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
import org.springframework.data.redis.core.RedisTemplate;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)       // 워밍업 1회
@Measurement(iterations = 5)  // 측정 3회
@State(Scope.Benchmark)
public class ChatLoadBenchmark {

    private static ConfigurableApplicationContext context;
    private final Long movingPlanId = 1L;
    private final Long messageId = 300L;
    private final PageRequest pageable = PageRequest.of(0, 10);
    private ChatRepository chatRepository;
    private RedisTemplate<String, Object> redisTemplate;

    @Setup(Level.Trial)
    public void setup() {
        if (context == null) {
            context = SpringApplication.run(BenchmarkApplication.class);
        }
        chatRepository = context.getBean(ChatRepository.class);
        redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    @Benchmark
    public List<Object> loadFromRedis() {
        return redisTemplate.opsForList().range("chat:1", 0, 9);
    }

    @Benchmark
    public List<Chat> loadFromMySQL_QueryMethod() {
        return chatRepository.findByMovingPlan_IdAndIdGreaterThanEqualOrderByIdAsc(
            movingPlanId, messageId, pageable);
    }

    @Benchmark
    public List<Chat> loadFromMySQL_JPQL() {
        return chatRepository.findChatsBeforeId(movingPlanId, messageId, pageable);
    }
}
