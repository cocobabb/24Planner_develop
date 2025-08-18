package com.example.p24zip.domain.chat.service;

import com.example.p24zip.domain.chat.dto.request.MessageRequestDto;
import com.example.p24zip.domain.chat.dto.response.ChatsResponseDto;
import com.example.p24zip.domain.chat.dto.response.MessageResponseDto;
import com.example.p24zip.domain.chat.dto.response.UserLastReadResponseDto;
import com.example.p24zip.domain.chat.entity.Chat;
import com.example.p24zip.domain.chat.repository.ChatRepository;
import com.example.p24zip.domain.movingPlan.entity.Housemate;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.CustomCode;
import com.example.p24zip.global.exception.CustomException;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import com.example.p24zip.global.notification.fcm.FcmService;
import com.example.p24zip.global.redis.RedisChatDto;
import com.example.p24zip.global.validator.MovingPlanValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.oauth2.sdk.GeneralException;
import java.io.IOException;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private static final String CHAT_MESSAGES_REDIS_HASH_KEY = "chat:%d"; // Redis에 저장된 만료일3일 메세지들 chat:{movingPlanId}
    private static final String CHAT_LAST_CURSOR_REDIS_HASH_KEY = "chat:%d:read:messageId:%s"; // 마지막으로 읽은 메세지 저장 chat:{movingPlanId}:read:messageId:{username}
    private static final String FCM_TOKEN_REDIS_SET_KEY = "%s:deviceTokens";
    private static final String FCM_RECEIVER_REDIS_SET_KEY = "chat:%d:connected";

    final MovingPlanValidator movingPlanValidator;

    private final MovingPlanRepository movingPlanRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private final FcmService fcmService;

    @Transactional
    public MessageResponseDto Chatting(
        Long movingPlanId,
        MessageRequestDto requestDto,
        String tokenUsername
    ) throws IOException, GeneralException {

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        User user = userRepository.findByUsername(tokenUsername)
            .orElseThrow(() -> new ResourceNotFoundException());

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        Chat chat = chatRepository.save(requestDto.toEntity(movingPlan, user));

        // FCM
        // 🔹 1. 채팅방 참여자 조회
        List<Housemate> participants = movingPlan.getHousemates();

        // 🔹 2. 현재 채팅방 접속자 목록 (Redis/WebSocket 세션 기반)
        List<String> connectedUsernames = getConnectedUsersFromRedis(movingPlanId);

        // 🔹 3. FCM 알림 발송 (채팅방에 없는 사람만)
        for (Housemate participant : participants) {
            if (participant.getId().equals(user.getId())) {
                continue; // 자기 자신 제외
            }
            if (connectedUsernames.contains(participant.getUser().getUsername())) {
                continue; // 접속 중이면 제외
            }

            String key = String.format(FCM_TOKEN_REDIS_SET_KEY,
                participant.getUser().getUsername());

            Set<String> deviceTokens = stringRedisTemplate.opsForSet()
                .members(key);

            System.out.println("chatting deviceToken: " + deviceTokens);

            if (deviceTokens != null) {
                for (String token : deviceTokens) {
                    fcmService.sendMessageTo(token, "새 메세지", requestDto.getText());
                }
            }
        }

        // Redis에 저장할 채팅 정보 가진 DTO 생성
        RedisChatDto redisChatDto = RedisChatDto.builder()
            .messageId(chat.getId())
            .movingPlanId(movingPlanId)
            .chatMessage(requestDto.getText())
            .writerId(user.getId())
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

        return MessageResponseDto.from(chat.getId(), text, user.getId(), user.getNickname(),
            createTime,
            createDay);
    }

    public List<String> getConnectedUsersFromRedis(Long movingPlanId) {
        String key = String.format(FCM_RECEIVER_REDIS_SET_KEY, movingPlanId);
        return new ArrayList<>(stringRedisTemplate.opsForSet().members(key));
    }


    /**
     * 사용자가 읽은 마지막 messageId 기준으로 다음 메세지들을 보여주는 메서드
     *
     * @param movingPlanId : 이사계획id
     * @param user         : 로그인하여 이용 중인 사용자
     * @param size         : 메세지들 받아올 size(개수)
     * @return ChatsResponseDto : 채팅에 전달될 데이터 형식 dto(마지막으로 읽은 메세지, 채팅dto List를 포함하고 있다)
     * @apiNote <p>[3일간 Redis에 저장되는 메세지]</p>
     * <p>Key : "chat:{이사계획 id}" </p>
     * <p>Field : "{채팅 메세지 id}" </p>
     * <p>Value : {message-id, 이사계획 id, 채팅 메세지 내용, 메시지
     * 작성자(닉네임), 채팅 작성된 시간}</p>
     * <p></p>
     * <p>[사용자가 마지막으로 읽은 messageId]</p>
     * <p>Key : "chat:{이사계획 id}:read:messageId:{사용자 아이디(username)}" </p>
     * <p>Field : "{채팅 메세지 id}" </p>
     * <p>Value : {message-id, 이사계획 id, 채팅 메세지 내용, 메시지
     * 작성자 id, 작성자 닉네임, 채팅 작성된 시간}</p>
     */
    public ChatsResponseDto readChats(Long movingPlanId, User user, int size) {

        movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        String key = String.format(CHAT_LAST_CURSOR_REDIS_HASH_KEY, movingPlanId,
            user.getUsername());

        Long lastReadMessageId;
        List<Chat> chats;
        // lastReadMessageId 저장된 기록 있는 경우
        if (redisTemplate.opsForHash().hasKey(key, user.getUsername())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            UserLastReadResponseDto readChat = objectMapper.convertValue(
                redisTemplate.opsForHash().get(key, user.getUsername()),
                UserLastReadResponseDto.class);

            lastReadMessageId = readChat.getMessageId();
            System.out.println("readChats-lastReadMessageId: " + lastReadMessageId);
        }
        // lastReadMessageId 저장된 기록 없으면(기존 사용자가 아닌 새로운 사용자) 최신 메세지 1개 보여주기
        else {
            lastReadMessageId = 0L;
            Pageable pageableForNew = PageRequest.of(0, 1, Sort.by(Direction.DESC, "id"));
            chats = chatRepository.findByMovingPlan_IdOrderByIdDesc(movingPlanId, pageableForNew);
            List<MessageResponseDto> chatList = changeListToMessageResponseDto(chats);
            System.out.println("MySQL 데이터 출력: 3일 지난 후 채팅방 방문");

            return ChatsResponseDto.from(lastReadMessageId, chatList);
        }

        // Redis에 최근 3일간의 메세지 보관되어 있으면 RDB가 아닌 레디스 데이터로 가져오기
        List<RedisChatDto> chat3daysAgo = getMessagesFromRedis(movingPlanId);

        boolean existsInRedis = chat3daysAgo.stream()
            .anyMatch(chat -> chat.getMessageId().equals(lastReadMessageId));

        // Redis에 해당하는 messageId가 있다면 레디스에서 가져오고 없으면 MySQL에서 메세지들 가져옴
        List<MessageResponseDto> chatList;
        if (existsInRedis) {
            chatList = changeListToMessageResponseDto(chat3daysAgo);
            System.out.println("다음 메세지 Redis 데이터 출력: 3일전에 채팅방 방문");

        } else {
            Pageable pageable = PageRequest.of(0, size, Sort.by(Direction.DESC, "id"));
            chats = chatRepository.findChatsAfterId(
                movingPlanId, lastReadMessageId, pageable);

            if (chats.size() == 1) {
                chats = chatRepository.findChatsAfterId(
                    movingPlanId, lastReadMessageId - size,
                    pageable);

            }
            chatList = changeListToMessageResponseDto(chats);
            System.out.println("다음 메세지 MySQL 데이터 출력: 3일 지난 후 채팅방 방문");
        }

        return ChatsResponseDto.from(lastReadMessageId, chatList);

    }

    @Transactional
    public void deleteChats(Long movingPlanId) {

        movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        chatRepository.deleteByMovingPlan_Id(movingPlanId);
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
        String key = String.format(CHAT_MESSAGES_REDIS_HASH_KEY, chatInfo.getMovingPlanId());

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
     * @apiNote <p>레디스에 저장된 채팅들을 RedisChatDto 리스트로 가져오는 메서드</p>
     */
    public List<RedisChatDto> getMessagesFromRedis(Long movingPlanId) {
        String key = String.format(CHAT_MESSAGES_REDIS_HASH_KEY, movingPlanId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return redisTemplate.opsForHash()
            .values(key)
            .stream()
            .map(obj -> {
                if (obj instanceof RedisChatDto) {
                    return (RedisChatDto) obj;
                } else {
                    return objectMapper.convertValue(obj, RedisChatDto.class);
                }
            })
            .sorted(Comparator.comparing(RedisChatDto::getMessageId))
            .toList();
    }


    /**
     * 특정 사용자가 마지막으로 읽은 메세지 UserLastReadResponseDto 객체를 Redis에 저장
     *
     * @param movingPlanId : 이사 계획 id
     * @param user         : 메세지를 읽고 있는 사용자
     * @param messageId    : 마지막으로 읽은 메세지 id
     * @return void
     * @apiNote <p> "chat:{movingPlanId}:read:messageId:사용자 아이디" 란 Key로 Redis Hash에 저장 </p>
     * <p>Redis Hash Field : 사용자 아이디(username)</p>
     * <p>Redis Hash Value : UserLastReadResponseDto
     * : {messageId(Chat의 id), 이사계획 id, 채팅 메세지 내용, 작성자 id,작성자 닉네임, 작성날짜}을 가진 객체</p>
     */
    public void saveLastCursorToRedis(Long movingPlanId, User user, Long messageId) {
        System.out.println("saveLastCursorToRedis-messageId: " + messageId);

        String key = String.format(CHAT_LAST_CURSOR_REDIS_HASH_KEY, movingPlanId,
            user.getUsername());

        // MySQL 에서 해당 메세지 데이터 읽어와서 UserLastReadResponseDto로 변환
        UserLastReadResponseDto readChat = readMessageCursor(key, user, messageId);

        // 기존에 저장된 메세지 기록 있으면 messageId 교체
        if (readChat.getFirstMessageId() != 0L) {
            readChat.changeMessageId(messageId);
            redisTemplate.opsForHash().put(key, user.getUsername(), readChat);
        } else {
            // 처음 메세지 저장하는 거면 fistMessageId == messageId
            readChat.setFirstMessageId(messageId);
            redisTemplate.opsForHash().put(key, user.getUsername(), readChat);
        }
    }


    /**
     * Redis에서 특정 사용자가 읽은 메세지id를 가진 데이터 가져와서 dto로 변환
     *
     * @param redisKey  : 마지막으로 읽은 메세지 정보를 레디스에서 가져오기 위한 key
     * @param user      : 메세지를 읽고 있는 사용자
     * @param messageId : 마지막으로 읽은 메세지 id
     * @return UserLastReadResponseDto : {messageId(Chat의 id), 이사계획 id, 채팅 메세지 내용, 작성자id, 작성자 닉네임,
     * 작성날짜 }을 가진 객체
     */
    public UserLastReadResponseDto readMessageCursor(String redisKey, User user, Long messageId) {

        // 저장된 기록 있는 경우
        if (redisTemplate.opsForHash().hasKey(redisKey, user.getUsername())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            UserLastReadResponseDto readChat = objectMapper.convertValue(
                redisTemplate.opsForHash().get(redisKey, user.getUsername()),
                UserLastReadResponseDto.class);

            if (!user.getNickname().equals(readChat.getWriterNickname())) {
                readChat.changeWriterNickname(user.getNickname());
            }
            return readChat;

        } else {
            Chat chat = chatRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException());

            if (chat != null) {
                // 처음 메세지 저장하는 경우 firsMessageId 0L로 되어 있음
                return UserLastReadResponseDto.builder()
                    .chat(chat)
                    .build();
            } else {
                return null;
            }
        }

    }


    /**
     * dto List 받아서 채팅에 쓰이는 형태의 dto List로 변환하여 반환
     *
     * @param chats : List<MessageResponseDto>로 변환할 리스트 객체
     * @return UserLastReadResponseDto : {messageId(Chat의 id), 채팅 메세지 내용, 작성자(닉네임), 작성시간, 작성 날짜 }을
     * 가진 객체 리스트
     */
    public List<MessageResponseDto> changeListToMessageResponseDto(List<?> chats) {

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        return chats.stream()
            .map(chat -> {
                if (chat instanceof RedisChatDto redisChat) {
                    return MessageResponseDto.from(
                        redisChat.getMessageId(),
                        redisChat.getChatMessage(),
                        redisChat.getWriterId(),
                        redisChat.getWriter(),
                        redisChat.getTimestamp()
                            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                            .format(formatterTime),
                        redisChat.getTimestamp()
                            .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                            .format(formatterDay)
                    );
                } else if (chat instanceof Chat entityChat) {
                    return MessageResponseDto.from(
                        entityChat.getId(),
                        entityChat.getText(),
                        entityChat.getUser().getId(),
                        entityChat.getUser().getNickname(),
                        entityChat.getCreatedAt().format(formatterTime),
                        entityChat.getCreatedAt().format(formatterDay)
                    );
                } else {
                    return null;
                }
            })
            .filter(Objects::nonNull) // null 제거
            .sorted(Comparator.comparing(MessageResponseDto::getMessageId))
            .toList();
    }

    /**
     * Client에서 보내온 messageId 기준으로 이전 메세지들 가져오는 메서드 (Redis에 있는 메세지 id이면 Redis에서 가져오고 없으면 MySQL에서
     * 메세지들을 가져온다)
     *
     * @param movingPlanId : 이사계획 id
     * @param user         : 로그인한 사용자
     * @param messageId    : 이전 메세지들 불러올 기준 messageId
     * @return ChatsResponseDto : 채팅에 전달될 데이터 형식 dto(마지막으로 읽은 메세지, 채팅dto List를 포함하고 있다)
     */
    public ChatsResponseDto getPreviousMessages(Long movingPlanId, User user, Long messageId) {

        movingPlanRepository.findById(movingPlanId)
            .orElseThrow(() -> new ResourceNotFoundException());

        String key = String.format(CHAT_LAST_CURSOR_REDIS_HASH_KEY, movingPlanId,
            user.getUsername());

        UserLastReadResponseDto lastReadChat = readMessageCursor(key, user, messageId);

        System.out.println("lastReadChat.getFirstMessageId(): " + lastReadChat.getFirstMessageId());
        System.out.println("받아온 메세지 아이디: " + messageId);

        if (messageId == lastReadChat.getFirstMessageId()) {
            throw new CustomException(CustomCode.FIRST_CHAT_MESSAGE);
        }

        // Redis에 메세지 보관되어 있으면 MySQL가 아닌 Redis 데이터로 가져오기
        List<RedisChatDto> chat3daysAgo = getMessagesFromRedis(movingPlanId);

        boolean existsInRedis = chat3daysAgo.stream()
            .anyMatch(chat -> chat.getMessageId().equals(messageId - 1));

        List<MessageResponseDto> chatList;
        if (existsInRedis) {

            chatList = changeListToMessageResponseDto(chat3daysAgo);
            System.out.println("이전 메세지 Redis 데이터 출력: 3일전에 채팅방 방문");

        } else {
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "id"));

            List<Chat> chats = chatRepository.findChatsBeforeId(
                movingPlanId, messageId, pageable);

            // Redis 커서 이하 메시지는 잘라냄 => 사용자가 처음 본 메세지까지만 볼 수 있음
            List<Chat> filtered = chats.stream()
                .filter(chat -> chat.getId() > lastReadChat.getFirstMessageId())
                .collect(Collectors.toList());

            chatList = changeListToMessageResponseDto(filtered);
            System.out.println("이전 메세지 MySQL 데이터 출력: 3일 지난 후 채팅방 방문");
        }

        return ChatsResponseDto.from(messageId, chatList);
    }
}
