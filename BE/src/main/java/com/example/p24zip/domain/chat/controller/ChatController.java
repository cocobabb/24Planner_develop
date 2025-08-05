package com.example.p24zip.domain.chat.controller;


import com.example.p24zip.domain.chat.dto.request.MessageRequestDto;
import com.example.p24zip.domain.chat.dto.response.ChatsResponseDto;
import com.example.p24zip.domain.chat.dto.response.MessageResponseDto;
import com.example.p24zip.domain.chat.service.ChatService;
import com.example.p24zip.domain.user.entity.User;
import com.example.p24zip.global.exception.StompTokenException;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    final MovingPlanValidator movingPlanValidator;
    private final ChatService chatService;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/chat/{movingPlanId}")
    @SendTo("/topic/{movingPlanId}")
    public MessageResponseDto chatting(
        StompHeaderAccessor headerAccessor,
        @DestinationVariable Long movingPlanId,
        MessageRequestDto requestDto) {

        String token = headerAccessor.getFirstNativeHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new StompTokenException(requestDto.getText());
        }
        String tokenUsername = jwtTokenProvider.getUsername(token);

        return chatService.Chatting(movingPlanId, requestDto, tokenUsername);
    }

    @GetMapping("/chats/{movingPlanId}/scroll")
    public ResponseEntity<ApiResponse<ChatsResponseDto>> readChats(@PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user,
        @RequestParam(required = false, defaultValue = "50") int size) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        return ResponseEntity.ok(
            ApiResponse.ok("OK",
                "댓글 조회에 성공했습니다.",
                chatService.readChats(movingPlanId, user, size)
            )
        );
    }

    // Redis에 사용자가 읽은 마지막 메세지id 저장
    @PostMapping("/chats/{movingPlanId}/lastCursor")
    public void saveLastCursorToRedis(
        @PathVariable Long movingPlanId, @AuthenticationPrincipal User user,
        @RequestParam Long messageId) {

        movingPlanValidator.validateMovingPlanAccess(movingPlanId, user);

        System.out.println("controller - saveLastCursorToRedis: " + messageId);
        chatService.saveLastCursorToRedis(movingPlanId, user, messageId);

    }

    // 일종의 채팅방 폭파(모든 사용자의 채팅방에서 채팅 내용 사라짐) => 이사계획 주인만 삭제 가능하고 각 채팅 메세지 삭제는 없음
    @DeleteMapping("/chats/{movingPlanId}")
    public ResponseEntity<ApiResponse<Object>> deleteChats(@PathVariable Long movingPlanId,
        @AuthenticationPrincipal User user) {

        movingPlanValidator.validateMovingPlanOwnership(movingPlanId, user);

        chatService.deleteChats(movingPlanId);

        return ResponseEntity.ok(
            ApiResponse.ok("DELETED", "댓글을 삭제했습니다.", null)
        );
    }
}
