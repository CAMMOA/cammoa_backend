package org.example.products.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupBuyingJoinRequest {

    @NotNull(message = "공동구매 ID는 필수입니다.")
    private Long postId;
}
