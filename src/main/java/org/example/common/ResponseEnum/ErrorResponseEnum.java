package org.example.common.ResponseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResponseEnum implements Response {

    RESPONSE_NOT_VALID(HttpStatus.BAD_REQUEST, "Response Is Not Valid"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Invalid Token"),

    //이메일
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed To Send Email"),
    EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST, "Email Verification Failed"),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "Email Not Verified"),

    //인증코드
    AUTH_CODE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication Code Cannot Be Found"),
    AUTH_CODE_MISMATCH(HttpStatus.BAD_REQUEST, "Authentication Code Does Not Match"),

    //redis
    REDIS_STORE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store authentication code in Redis"),

    //사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User Not Found"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "Invalid Password"),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "You do not have permission to access this post"),
    PASSWORDS_DO_NOT_MATCH(HttpStatus.BAD_REQUEST, "Passwords Do Not Match"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "Invalid Email"),

    //게시글
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post not found"),
    INVALID_DEADLINE(HttpStatus.BAD_REQUEST, "Deadline must be after the current time"),
    INVALID_MAX_PARTICIPANTS(HttpStatus.BAD_REQUEST, "Max participants must be greater than current participants"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Invalid category"),

    //중복된 리소스
    DUPLICATED_USERNAME(HttpStatus.CONFLICT , "Duplicated username"),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "Duplicated nickname"),
    DUPLICATED_EMAIL(HttpStatus.CONFLICT , "Duplicated email"),
    DUPLICATED_PARTICIPANT(HttpStatus.CONFLICT , "Duplicated participant"),

    //채팅방
    CHATROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "Chatroom Not Found"),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Particiant Not Found"),

    //공동구매 참여
    ALREADY_JOINED(HttpStatus.BAD_REQUEST, "You have already joined this group buying."),
    POST_CLOSED(HttpStatus.BAD_REQUEST, "This group buying is already closed."),
    POST_FULL(HttpStatus.BAD_REQUEST, "This group buying has reached the maximum number of participants."),
    NOT_JOINED(HttpStatus.BAD_REQUEST, "You have not joined this group buying and cannot cancel participation."),

    //알림
    POST_NOT_COMPLETED(HttpStatus.BAD_REQUEST, "Group buying is not yet completed"),

    //검색
    INVALID_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, "Invalid search condition. Please provide a keyword or category.");

    private final HttpStatus httpStatus;
    private final String message;
}
