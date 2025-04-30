package org.example.chat.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.common.repository.entity.BaseTimeEntity;
import org.example.products.repository.entity.ProductEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_room")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatRoomEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    private String chatRoomName;

    @OneToOne
    @JoinColumn(name = "product_id", unique = true, nullable = false)
    private ProductEntity product;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<ChatParticipantEntity> chatParticipants = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatMessageEntity> chatMessages = new ArrayList<>();
}
