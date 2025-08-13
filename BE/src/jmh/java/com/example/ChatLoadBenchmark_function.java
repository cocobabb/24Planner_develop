package com.example;

import com.example.p24zip.domain.chat.dto.response.ChatsResponseDto;
import com.example.p24zip.domain.chat.service.ChatService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
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

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime, Mode.Throughput})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class ChatLoadBenchmark_function {

    private static ConfigurableApplicationContext context;
    private Long movingPlanId = 3L;
    private ChatService chatService;
    private UserRepository userRepository;
    private User user;

    // 상태 유지용 messageId
    private long messageId = 783L;

    @Setup(Level.Trial)
    public void setup() {
        if (context == null) {
            context = SpringApplication.run(BenchmarkApplication.class);
        }
        this.userRepository = context.getBean(UserRepository.class);
        this.chatService = context.getBean(ChatService.class);
        this.user = userRepository.findByUsername("test@test.com").get();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    // 이전 메시지 조회
    @Benchmark
    public ChatsResponseDto benchmarkGetPreviousMessages() {
        return chatService.getPreviousMessages(
            movingPlanId,
            user,
            messageId
        );
    }

    // 매 호출 후 messageId 증가
    @TearDown(Level.Invocation)
    public void increaseMessageId() {
        if (messageId < 837L) {
            messageId += 50;
            if (messageId > 837L) {
                messageId = 837L;
            }
        }
    }

    // 다음 메시지 조회
    @Benchmark
    public ChatsResponseDto benchmarkGetNextMessages() {
        return chatService.readChats(
            movingPlanId,
            user,
            50
        );
    }
}
