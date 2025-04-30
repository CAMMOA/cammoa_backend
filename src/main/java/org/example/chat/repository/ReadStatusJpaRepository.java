package org.example.chat.repository;

import org.example.chat.repository.entity.ReadStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusJpaRepository extends JpaRepository<ReadStatusEntity, Long> {
    ReadStatusEntity save(ReadStatusEntity readStatus);
}
