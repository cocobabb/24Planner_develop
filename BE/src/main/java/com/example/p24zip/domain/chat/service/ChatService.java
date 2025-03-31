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
            String tokenusername
            ) {

        MovingPlan movingPlan = movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException());

        User user = userRepository.findByUsername(tokenusername)
                .orElseThrow(() -> new ResourceNotFoundException());

//        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);


        Chat chat = chatRepository.save(requestDto.toEntity(movingPlan, user));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String createTime = chat.getCreatedAt().format(formatter);

        String text = HtmlUtils.htmlEscape(chat.getText());

        return MessageResponseDto.from(text, user.getNickname(), createTime);
    }

    public ChatsResponseDto readchats(Long movingPlanId) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException());

        List<Chat> chats = chatRepository.findAllById(movingPlanId);

        List<MessageResponseDto> chatlist =
                chats.stream()
                        .map(chat -> MessageResponseDto.from(chat.getText(), chat.getUser().getNickname(), chat.getCreatedAt().format(formatter)))
                        .toList();

        return ChatsResponseDto.from(chatlist);
    }

    @Transactional
    public void deletechats(Long movingPlanId) {

        movingPlanRepository.findById(movingPlanId)
                .orElseThrow(() -> new ResourceNotFoundException());

        chatRepository.deletechattingplan(movingPlanId);
    }
}
