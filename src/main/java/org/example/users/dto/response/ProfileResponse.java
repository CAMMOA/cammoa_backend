package org.example.users.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class ProfileResponse {

    private String nickname;
    private String email;
    private List<?> myGroupBuyings;      // 작성한 공동구매 목록 (지금은 빈 리스트)
    private List<?> joinedGroupBuyings;  // 참여한 공동구매 목록 (지금은 빈 리스트)
    private List<?> notifications;       // 나의 알림 목록 (지금은 빈 리스트)
}
