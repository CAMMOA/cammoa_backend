package org.example.products.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.example.products.dto.request.GroupBuyingJoinRequest;
import org.example.products.service.ProductService;
import org.example.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-buyings")
@RequiredArgsConstructor
public class ParticipationController {

    private final ProductService productService;
    private final JwtTokenProvider jwtTokenProvider;

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
}
