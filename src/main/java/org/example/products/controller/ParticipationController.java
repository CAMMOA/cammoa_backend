package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/join")
    public ResponseEntity<?> joinGroupBuying(@RequestBody @Valid GroupBuyingJoinRequest request,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        Long userId = jwtTokenProvider.getUserId(token);

        productService.joinGroupBuying(request.getPostId(), userId);

        return ResponseEntity.ok(
                CommonResponseEntity.builder()
                        .response(SuccessResponseEnum.JOIN_SUCCESS)
                        .build()
        );
    }

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
                        .response(SuccessResponseEnum.JOIN_SUCCESS) // 또는 별도의 응답 메시지 enum 추가
                        .data(response)
                        .build()
        );

    }
}
