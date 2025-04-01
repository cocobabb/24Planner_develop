package com.example.p24zip.domain.chat.controller;


import com.example.p24zip.domain.chat.dto.request.MessageRequestDto;
import com.example.p24zip.domain.chat.dto.response.ChatsResponseDto;
import com.example.p24zip.domain.chat.dto.response.MessageResponseDto;
import com.example.p24zip.domain.chat.service.ChatService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.response.ApiResponse;
import com.example.p24zip.global.security.jwt.JwtTokenProvider;
import com.example.p24zip.global.validator.MovingPlanValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;
    final MovingPlanValidator movingPlanValidator;



    @MessageMapping("/chat/{movingPlanId}")
    @SendTo("/topic/{movingPlanId}")
    public MessageResponseDto chatting(
            StompHeaderAccessor headerAccessor,
            @DestinationVariable Long movingPlanId,
            MessageRequestDto requestDto) {

        String token = headerAccessor.getFirstNativeHeader("Authorization");
        String tokenusername = jwtTokenProvider.getUsername(token);

        return chatService.Chatting(movingPlanId, requestDto, tokenusername);
    }

    @GetMapping("/chats/{movingPlanId}")
    public ResponseEntity<ApiResponse<ChatsResponseDto>> readchats(@PathVariable Long movingPlanId, @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
                ApiResponse.ok("OK",
                        "조회에 성공했습니다.",
                        chatService.readchats(movingPlanId))
        );
    }

    @DeleteMapping("/chats/{movingPlanId}")
    public ResponseEntity<ApiResponse<Object>> deletechats(@PathVariable Long movingPlanId, @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanOwnership(movingPlanId, user);

        chatService.deletechats(movingPlanId);

        return ResponseEntity.ok(
                ApiResponse.ok("DELETED","내용을 삭제했습니다.",null)
        );
    }
}
