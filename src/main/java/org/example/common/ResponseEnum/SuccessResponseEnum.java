package org.example.common.ResponseEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.common.Response;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessResponseEnum implements Response {
    RESOURCES_CREATED(HttpStatus.CREATED, "Resources Created"),
    REQUEST_SUCCESS(HttpStatus.OK, "Request Processed Successfully"),
    RESOURCES_GET(HttpStatus.OK, "Resourses Is Got Successfully"),

    LOGIN_SUCCESS(HttpStatus.OK, "Login Successful"),
    PASSWORD_CHANGED(HttpStatus.OK, "Password Changed"),
    WITHDRAWAL_SUCCESS(HttpStatus.OK, "User Withdrawal Success"),

    EMAIL_SEND_SUCCESS(HttpStatus.OK, "Email Successfully Sent"),
    EMAIL_VERIFICATION_SUCCESS(HttpStatus.OK, "Email Verification Successed"),

    //게시글
    POST_DELETE_SUCCESS(HttpStatus.OK, "Post Deleted Successfully"),
    //공동구매 참여
    JOIN_SUCCESS(HttpStatus.OK, "You have successfully joined the group buying."),
    CHATROOM_JOIN_SUCCESS(HttpStatus.OK, "Joined The Chat Room Successfully"),
    CANCEL_SUCCESS(HttpStatus.OK, "Successfully canceled group buying participation."),

    //채팅방
    PARTICIPANT_LEAVED(HttpStatus.OK, "Participant Leaved Successfully");
    private final HttpStatus httpStatus;
    private final String message;
}
