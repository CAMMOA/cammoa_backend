package org.example;

import lombok.RequiredArgsConstructor;
import org.example.products.repository.ProductRepository;
import org.example.products.repository.ParticipationRepository;
import org.example.products.repository.entity.CategoryEnum;
import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.entity.ParticipationEntity;
import org.example.users.repository.UserRepository;
import org.example.users.repository.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ParticipationRepository participationRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // 1. 사용자 생성
            UserEntity user1 = UserEntity.builder()
                    .nickname("홍길동")
                    .username("testuser1")
                    .email("test1@hufs.ac.kr")
                    .password("hashed_pw_1")
                    .build();

            UserEntity user2 = UserEntity.builder()
                    .nickname("김철수")
                    .username("testuser2")
                    .email("test2@hufs.ac.kr")
                    .password("hashed_pw_2")
                    .build();

            userRepository.save(user1);
            userRepository.save(user2);

            // 2. 게시글 생성
            ProductEntity product1 = ProductEntity.builder()
                    .title("생수 공구 구함!")
                    .description("무거운 생수 같이 사요")
                    .category(CategoryEnum.LIVING)
                    .price(8000)
                    .deadline(LocalDateTime.of(2025, 6, 1, 23, 59, 59))
                    .place("미파닭 앞")
                    .currentParticipants(1)
                    .maxParticipants(3)
                    .status("OPEN")
                    .image("/images/bfb49e53-806c-49aa-a24a-c0ba699a5c54_생수.jpg")
                    .user(user1)
                    .build();

            ProductEntity product2 = ProductEntity.builder()
                    .title("세제 대용량 공구해요")
                    .description("빨래 많이 하는 자취생분들 함께 사요!")
                    .category(CategoryEnum.LIVING)
                    .price(18000)
                    .deadline(LocalDateTime.of(2025, 6, 14, 23, 59, 59))
                    .place("기숙사 1동 앞")
                    .currentParticipants(1)
                    .maxParticipants(4)
                    .status("OPEN")
                    .image("/images/005b7374-9fd5-4e0b-943f-cefd45278e54_세제.jpg")
                    .user(user1)
                    .build();

            ProductEntity product3 = ProductEntity.builder()
                    .title("포카리 싸게 사요")
                    .description("이온음료 공구해서 마셔요")
                    .category(CategoryEnum.LIVING)
                    .price(12000)
                    .deadline(LocalDateTime.of(2025, 6, 10, 23, 59, 59))
                    .place("모현읍")
                    .currentParticipants(0)
                    .maxParticipants(2)
                    .status("OPEN")
                    .image("/images/ec504076-1ba0-4fb3-a7d3-263997b2ebc8_포카리.jpg")
                    .user(user2)
                    .build();

            ProductEntity product4 = ProductEntity.builder()
                    .title("면도기 공구해요")
                    .description("도루코 슬릭 면도기 세트 싸게 구해요")
                    .category(CategoryEnum.LIVING)
                    .price(9000)
                    .deadline(LocalDateTime.of(2025, 6, 12, 23, 59, 59))
                    .place("한대앞역")
                    .currentParticipants(1)
                    .maxParticipants(3)
                    .status("OPEN")
                    .image("/images/7a9a9c65-1294-4968-92b8-58baff0ca4f9_면도기.jpg")
                    .user(user1)
                    .build();

            ProductEntity product5 = ProductEntity.builder()
                    .title("닭가슴살 10팩 묶음 공구")
                    .description("단백질 충전! 다양한 맛 선택 가능!")
                    .category(CategoryEnum.FOOD)
                    .price(20000)
                    .deadline(LocalDateTime.of(2025, 6, 25, 23, 59, 59))
                    .place("한식당 앞")
                    .currentParticipants(1)
                    .maxParticipants(4)
                    .status("OPEN")
                    .image("/images/9fc7f73e-2bf3-4d43-b7fb-7113ee6f9f98_닭가슴살.webp")
                    .user(user1)
                    .build();

            ProductEntity product6 = ProductEntity.builder()
                    .title("다이어트 도시락 공구!")
                    .description("헬스하는 친구들 같이 주문해요")
                    .category(CategoryEnum.FOOD)
                    .price(25000)
                    .deadline(LocalDateTime.of(2025, 6, 22, 23, 59, 59))
                    .place("헬스장 앞")
                    .currentParticipants(2)
                    .maxParticipants(5)
                    .status("OPEN")
                    .image("/images/42c09fa8-5dea-4988-a744-a8eafa1877a1_다이어트 도시락.webp")
                    .user(user2)
                    .build();

            ProductEntity product7 = ProductEntity.builder()
                    .title("감자칩 6종 세트 공구")
                    .description("오리지널부터 사워크림까지 골고루 있어요~")
                    .category(CategoryEnum.FOOD)
                    .price(9900)
                    .deadline(LocalDateTime.of(2025, 6, 18, 23, 59, 59))
                    .place("복지관 앞")
                    .currentParticipants(0)
                    .maxParticipants(5)
                    .status("OPEN")
                    .image("/images/6c13b0e7-87d3-4aec-9240-66ee76b8cedd_감자칩.jpg")
                    .user(user2)
                    .build();

            ProductEntity product8 = ProductEntity.builder()
                    .title("군고구마 나눠요")
                    .description("겨울 간식 미리 준비해요~ 따끈따끈!")
                    .category(CategoryEnum.FOOD)
                    .price(6000)
                    .deadline(LocalDateTime.of(2025, 6, 20, 23, 59, 59))
                    .place("모현동 GS25 앞")
                    .currentParticipants(3)
                    .maxParticipants(6)
                    .status("OPEN")
                    .image("/images/b00e352c-5974-47fb-82b5-cf3ecc786677_군고구마.jpg")
                    .user(user1)
                    .build();

            productRepository.save(product1);
            productRepository.save(product2);
            productRepository.save(product3);
            productRepository.save(product4);
            productRepository.save(product5);
            productRepository.save(product6);
            productRepository.save(product7);
            productRepository.save(product8);

            // 3. 참여 정보 생성 (user2가 product1에 참여)
            ParticipationEntity participation = ParticipationEntity.builder()
                    .user(user2)
                    .product(product1)
                    .joinedAt(LocalDateTime.of(2025, 5, 20, 11, 0, 0))
                    .build();

            participationRepository.save(participation);
        };
    }
}
