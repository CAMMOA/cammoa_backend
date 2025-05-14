package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.chat.dto.response.JoinChatRoomResponse;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.products.dto.request.GroupBuyingJoinRequest;
import org.example.products.service.ParticipationService;
import org.example.products.service.ProductService;
import org.example.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/group-buyings")
@RequiredArgsConstructor
public class ParticipationController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ParticipationService participationService;

    //공동구매 참여
    @PostMapping("/join")
    public ResponseEntity<?> joinGroupBuying(@RequestBody @Valid GroupBuyingJoinRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);

        JoinChatRoomResponse response = productService.joinGroupBuying(request.getPostId(), userId);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.JOIN_SUCCESS)
                        .data(response)
                        .build()
        );
    }

    //공동구매 상태 확인(최대 인원 달성 여부)
    @GetMapping("/{group_buy_id}/status")
    public ResponseEntity<?> checkParticipationStatus(@PathVariable("group_buy_id") Long postId,
                                                      @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);

        boolean isJoined = participationService.checkParticipationStatus(postId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("isJoined", isJoined);

        return ResponseEntity.ok(
                CommonResponseEntity.<Map<String, Object>>builder()
                        .response(SuccessResponseEnum.JOIN_SUCCESS)
                        .data(response)
                        .build()
        );

    }

    // 공동구매 참여 취소
    @DeleteMapping("/{group_buy_id}/participants/{user_id}")
    public ResponseEntity<?> cancelParticipation(@PathVariable("group_buy_id") Long postId,
                                                 @PathVariable("user_id") Long userId) {

        participationService.cancelParticipation(postId, userId);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.CANCEL_SUCCESS)
                        .build()
        );
    }

    // 공동구매 모집 완료 이메일 알림 전송
    @PostMapping("/{group_buy_id}/notify")
    public ResponseEntity<?> notifyGroupBuyingCompletion(@PathVariable("group_buy_id") Long postId) {

        productService.notifyGroupBuyingCompletion(postId);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.EMAIL_NOTIFICATION_SENT)
                        .build()
        );
    }


}
