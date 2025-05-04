package org.example.chat.controller;

import lombok.AllArgsConstructor;
import org.example.chat.dto.request.CreateChatRoomRequest;
import org.example.chat.dto.response.CreateChatRoomResponse;
//import org.example.chat.dto.response.GetChatRoomsResponse;
import org.example.chat.service.ChatService;
import org.example.common.ResponseEnum.SuccessResponseEnum;
import org.example.common.repository.entity.CommonResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
@AllArgsConstructor
public class ChatController {
    private final ChatService chatService;

    //채팅방 개설
    @PostMapping("/rooms/create")
    public ResponseEntity<?> createChatRoom(@RequestBody CreateChatRoomRequest request) {
        CreateChatRoomResponse response = chatService.createChatRoom(request.getProductId(), request.getChatRoomName());
        return ResponseEntity.ok(
                CommonResponseEntity.<CreateChatRoomResponse>builder()
                        .data(response)
                        .response(SuccessResponseEnum.RESOURCES_CREATED)
                        .build()
        );
    }

}
