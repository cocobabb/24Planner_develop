package com.example.p24zip.domain.chat.service;

import com.example.p24zip.domain.chat.dto.request.MessageRequestDto;
import com.example.p24zip.domain.chat.dto.response.ChatsResponseDto;
import com.example.p24zip.domain.chat.dto.response.MessageResponseDto;
import com.example.p24zip.domain.chat.dto.response.UserLastReadResponseDto;
import com.example.p24zip.domain.chat.entity.Chat;
import com.example.p24zip.domain.chat.repository.ChatRepository;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomErrorCode;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import com.example.p24zip.global.redis.RedisChatDto;
import com.example.p24zip.global.validator.MovingPlanValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private static final String REDIS_HASH_KEY_FORMAT = "chat:%d"; // 기본 포맷 chat:{movingPlanId}
    final MovingPlanValidator movingPlanValidator;
    private final MovingPlanRepository movingPlanRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public MessageResponseDto Chatting(
        Long movingPlanId,
        MessageRequestDto requestDto,
        String tokenUsername
    ) {

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        User user = userRepository.findByUsername(tokenUsername)
            .orElseThrow(() -> new ResourceNotFoundException());

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        Chat chat = chatRepository.save(requestDto.toEntity(movingPlan, user));

        // Redis에 저장할 채팅 정보 가진 DTO 생성
        RedisChatDto redisChatDto = RedisChatDto.builder()
            .messageId(chat.getId())
            .movingPlanId(movingPlanId)
            .chatMessage(requestDto.getText())
            .writer(user.getNickname())
            .timestamp(chat.getCreatedAt())
            .build();
        // 최근 채팅 기록은 Redis 통해 불러오기 위해 Redis 해시에 따로 저장
        saveChatToRedis(redisChatDto);

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        String createTime = chat.getCreatedAt().format(formatterTime);

        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern(
            "yyyy" + "년 " + "MM" + "월 " + "dd" + "일");
        String createDay = chat.getCreatedAt().format(formatterDay);

        String text = HtmlUtils.htmlEscape(chat.getText());

        return MessageResponseDto.from(chat.getId(), text, user.getNickname(), createTime,
            createDay);
    }


    // 메세지 아이디 기준 다음 메세지들 가져오기
    public ChatsResponseDto readChats(Long movingPlanId, User user, int size) {

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern(
            "yyyy" + "년 " + "MM" + "월 " + "dd" + "일");

        movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        // 사용자가 마지막으로 읽은 messageId
        Long messageId;
        UserLastReadResponseDto lastRead = getLastCursorFromRedis(movingPlanId, user);
        if (lastRead != null) {
            messageId = lastRead.getMessageId();
        } else {
            messageId = 0L;
        }

        // Redis에 최근 3일간의 메세지 보관되어 있으면 RDB가 아닌 레디스 데이터로 가져오기
        List<RedisChatDto> chat3daysAgo = getMessagesFromRedis(movingPlanId);

        List<MessageResponseDto> chatList;
        if (chat3daysAgo != null && !chat3daysAgo.isEmpty()) {
            chatList =
                chat3daysAgo.stream()
                    .map(chat -> MessageResponseDto.from(
                        chat.getMessageId(),
                        chat.getChatMessage(),
                        chat.getWriter(),
                        chat.getTimestamp().withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                            .format(formatterTime),
                        chat.getTimestamp().withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                            .format(formatterDay)))
                    .toList();
            System.out.println("Redis 데이터 출력: 3일전에 채팅방 방문");
        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));

            List<Chat> chats;
            if (messageId == 0L) {
                chats = chatRepository.findRecentChats(movingPlanId, pageable);
            } else {
                chats = chatRepository.findChatsAfterId(movingPlanId, messageId, pageable);
            }
            chatList =
                chats.stream()
                    .map(chat -> MessageResponseDto.from(
                        chat.getId(),
                        chat.getText(),
                        chat.getUser().getNickname(),
                        chat.getCreatedAt().format(formatterTime),
                        chat.getCreatedAt().format(formatterDay)))
                    .sorted(Comparator.comparing(MessageResponseDto::getMessageId)) // 아이디 오름차순 정렬
                    .toList();
            System.out.println("MySQL 데이터 출력: 3일 지난 후 채팅방 방문");
        }
        return ChatsResponseDto.from(chatList);
    }

    @Transactional
    public void deleteChats(Long movingPlanId) {

        movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        chatRepository.deleteChattingPlan(movingPlanId);
    }

    /**
     * 채팅을 Redis 해시에 저장하면서 만료기간 3일 지정
     *
     * @return void
     * @apiNote <p>Key : chat:이사계획 id </p>
     * <p>Field : 채팅 메세지 id </p>
     * <p>Value : {message-id, 이사계획 id, 채팅 메세지 내용, 메시지
     * 작성자(닉네임), 채팅 작성된 시간}</p>
     */
    public void saveChatToRedis(RedisChatDto chatInfo) {
        String key = String.format(REDIS_HASH_KEY_FORMAT, chatInfo.getMovingPlanId());

        redisTemplate.opsForHash().put(
            key,
            chatInfo.getMessageId(),
            chatInfo
        );

        redisTemplate.expire(key, Duration.ofDays(3));
    }

    /**
     * Redis에서 최근 3일간 채팅 데이터 객체 리스트 가져오는 메서드
     *
     * @param movingPlanId : 이사 계획 id
     * @return List<RedisChatDto> :  {messageId(Chat의 id), 이사계획 id, 채팅 메세지 내용, 작성자 닉네임, 작성날짜}를 가진 객체
     * 리스트
     */
    public List<RedisChatDto> getMessagesFromRedis(Long movingPlanId) {
        String key = String.format(REDIS_HASH_KEY_FORMAT, movingPlanId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return redisTemplate.opsForHash()
            .values(key)
            .stream()
            .map(obj -> {
                if (obj instanceof RedisChatDto) {
                    return (RedisChatDto) obj;
                } else {
                    // LinkedHashMap 등으로 역직렬화된 경우 ObjectMapper로 변환
                    return objectMapper.convertValue(obj, RedisChatDto.class);
                }
            })
            .sorted(Comparator.comparing(RedisChatDto::getMessageId)) // 아이디 오름차순 정렬
            .toList();
    }


    /**
     * 특정 사용자가 읽은 메세지id를 가진 객체 읽은 사용자 필드를 가진 객체를 Redis에 저장
     *
     * @param movingPlanId : 이사 계획 id
     * @param user         : 메세지를 읽고 있는 사용자
     * @param messageId    : 마지막으로 읽은 메세지 id
     * @return void
     * @apiNote <p> "chat:{movingPlanId}:read:messageId" 란 Key로 Redis Hash에 저장 </p>
     * <p>Redis Hash Field : messageId</p>
     * <p>Redis Hash Value : UserLastReadResponseDto
     * : {messageId(Chat의 id), 이사계획 id, 채팅 메세지 내용, 작성자 닉네임, 작성날짜, 읽은 사용자 닉네임}을 가진 객체</p>
     */
    public void saveLastCursorToRedis(Long movingPlanId, User user, Long messageId) {
        UserLastReadResponseDto readChat = readMessageCursor(movingPlanId, user, messageId);

        String key = String.format(REDIS_HASH_KEY_FORMAT, readChat.getMovingPlanId());
        redisTemplate.opsForHash().put(key + ":read:messageId", readChat.getMessageId(), readChat);
    }

    public UserLastReadResponseDto getLastCursorFromRedis(Long movingPlanId, User user) {
        String key = String.format(REDIS_HASH_KEY_FORMAT, movingPlanId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        Optional<UserLastReadResponseDto> dto = redisTemplate.opsForHash()
            .values(key + ":read:messageId")
            .stream()
            .map(obj -> {
                if (obj instanceof UserLastReadResponseDto) {
                    return (UserLastReadResponseDto) obj;
                } else {
                    // LinkedHashMap 등으로 역직렬화된 경우 ObjectMapper로 변환
                    return objectMapper.convertValue(obj, UserLastReadResponseDto.class);
                }
            })
            .filter(readChats -> readChats.getReader().equals(user.getNickname()))
            .findFirst();

        return dto.orElse(null);
    }

    /**
     * 특정 사용자가 읽은 메세지id를 가진 객체 읽은 사용자 필드를 가진 객체로 변환
     *
     * @param movingPlanId : 이사 계획 id
     * @param user         : 메세지를 읽고 있는 사용자
     * @param messageId    : 마지막으로 읽은 메세지 id
     * @return UserLastReadResponseDto : {messageId(Chat의 id), 이사계획 id, 채팅 메세지 내용, 작성자 닉네임, 작성날짜, 읽은
     * 사용자 닉네임}을 가진 객체
     */
    public UserLastReadResponseDto readMessageCursor(Long movingPlanId, User user, Long messageId) {

        return chatRepository.findByMovingPlanId(movingPlanId).stream()
            .filter(chat -> chat.getId() == messageId)
            .findFirst()
            .map(chatInfo -> new UserLastReadResponseDto(chatInfo, user.getNickname()))
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_SEARCH_CHAT_MESSAGE));

    }

}
