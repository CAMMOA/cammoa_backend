package org.example.chat.repository;

import lombok.AllArgsConstructor;
import org.example.chat.repository.entity.ReadStatusEntity;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ReadStatusRepositoryImpl implements ReadStatusRepository {

    private final ReadStatusJpaRepository readStatusJpaRepository;

    @Override
    public ReadStatusEntity save(ReadStatusEntity readStatus){return readStatusJpaRepository.save(readStatus);}
}
