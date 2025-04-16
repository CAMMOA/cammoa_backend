package org.example.products.repository;

import org.example.products.repository.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // 오늘의 공동구매 추천: DB에서 랜덤으로 가져옴옴
    @Query(value = "SELECT * FROM product_entity ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<ProductEntity> findRandomRecommendedProducts();


    // 곧 마감되는 공구: 마감일이 오늘 이후인 공구 중 마감일이 가까운 순
    @Query("SELECT p FROM ProductEntity p WHERE p.deadline >= CURRENT_DATE ORDER BY p.deadline ASC")
    List<ProductEntity> findClosingSoonProducts();

    // 방금 올라온 공구: 최근 24시간 내에 생성된 공구
    @Query("SELECT p FROM ProductEntity p WHERE p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<ProductEntity> findRecentProducts(@Param("since") LocalDateTime since);

    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.user WHERE p.productId = :productId")
    Optional<ProductEntity> findByIdWithUser(@Param("productId") Long productId);

    List<ProductEntity> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

}
