package com.example.p24zip.domain.chat.service;

import com.example.p24zip.domain.chat.dto.request.MessageRequestDto;
import com.example.p24zip.domain.chat.dto.response.ChatsResponseDto;
import com.example.p24zip.domain.chat.dto.response.MessageResponseDto;
import com.example.p24zip.domain.chat.entity.Chat;
import com.example.p24zip.domain.chat.repository.ChatRepository;
import com.example.p24zip.domain.movingPlan.entity.MovingPlan;
import com.example.p24zip.domain.movingPlan.repository.MovingPlanRepository;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.domain.user.repository.UserRepository;
import com.example.p24zip.global.exception.ResourceNotFoundException;
import com.example.p24zip.global.validator.MovingPlanValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final MovingPlanRepository movingPlanRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    final MovingPlanValidator movingPlanValidator;


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

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        String createTime = chat.getCreatedAt().format(formatterTime);

        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy" + "년 " + "MM" + "월 " + "dd" + "일");
        String createDay = chat.getCreatedAt().format(formatterDay);

        String text = HtmlUtils.htmlEscape(chat.getText());

        return MessageResponseDto.from(text, user.getNickname(), createTime, createDay);
    }

    public ChatsResponseDto readChats(Long movingPlanId) {

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("yyyy" + "년 " + "MM" + "월 " + "dd" + "일");

        movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException());

        List<Chat> chats = chatRepository.findAllById(movingPlanId);

        List<MessageResponseDto> chatlist =
                chats.stream()
                        .map(chat -> MessageResponseDto.from(chat.getText(), chat.getUser().getNickname(), chat.getCreatedAt().format(formatterTime), chat.getCreatedAt().format(formatterDay)))
                        .toList();

        return ChatsResponseDto.from(chatlist);
    }

    @Transactional
    public void deleteChats(Long movingPlanId) {

        movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException());

        chatRepository.deleteChattingPlan(movingPlanId);
    }
}
