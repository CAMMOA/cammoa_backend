package org.example.products.repository;

import org.example.products.repository.entity.ProductEntity;
import org.example.products.repository.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImageEntity, Long> {

    // 특정 게시글에 연관된 모든 이미지 조회
    List<ProductImageEntity> findAllByProduct(ProductEntity product);
    Optional<ProductImageEntity> findByProductAndImageUrl(ProductEntity product, String imageUrl);

}
