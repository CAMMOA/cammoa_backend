package org.example.chat.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.common.repository.entity.BaseTimeEntity;
import org.example.users.repository.entity.UserEntity;

@Entity
@Table(name = "read_status")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ReadStatusEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoomEntity chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessageEntity chatMessage;

    @Column(nullable = false)
    private Boolean isRead;
}
