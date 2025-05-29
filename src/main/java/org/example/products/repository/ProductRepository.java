package org.example.products.repository;

import org.example.products.repository.entity.CategoryEnum;
import org.example.products.repository.entity.ProductEntity;
import org.example.users.repository.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 오늘의 공동구매 추천: DB에서 랜덤으로 가져옴
    @Query(value = "SELECT * FROM product_entity WHERE deleted_at IS NULL ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<ProductEntity> findRandomRecommendedProducts();

    // 곧 마감되는 공구: 마감일이 오늘 이후인 공구 중 마감일이 가까운 순
    @Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL AND p.deadline >= CURRENT_DATE ORDER BY p.deadline ASC")
    Page<ProductEntity> findClosingSoonProducts(Pageable pageable);

    // 방금 올라온 공구
    @Query("SELECT p FROM ProductEntity p WHERE p.deletedAt IS NULL ORDER BY p.createdAt DESC")
    Page<ProductEntity> findRecentProducts(Pageable pageable);

    //게시글 검식 시 카테고리, 추천, 최신, 마감순 조회
    @Query("SELECT p FROM ProductEntity p " +
            "WHERE p.deletedAt IS NULL " +
            "AND (:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.description LIKE %:keyword%) " +
            "AND (:category IS NULL OR p.category = :category) " +
            "ORDER BY " +
            "CASE WHEN :sortType = 'DEADLINE' THEN p.deadline END ASC, " +
            "CASE WHEN :sortType = 'RECENT' THEN p.createdAt END DESC, " +
            "CASE WHEN :sortType = 'RECOMMEND' THEN function('RAND') END")
    List<ProductEntity> searchProductsByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("category") CategoryEnum category, // 여기 category를 String 말고 CategoryEnum으로 직접
            @Param("sortType") String sortType
    );
    List<ProductEntity> findByUser(UserEntity user);

    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.user WHERE p.productId = :postId AND p.deletedAt IS NULL")
    Optional<ProductEntity> findByIdWithUserAndNotDeleted(@Param("postId") Long postId);

    List<ProductEntity> findByCategoryAndDeletedAtIsNull(CategoryEnum category);
    List<ProductEntity> findByDeletedAtIsNull(); // 전체 목록 조회 시 사용
    List<ProductEntity> findByUserAndDeletedAtIsNull(UserEntity user);


}

