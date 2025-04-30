package org.example.chat.repository;

import org.example.chat.repository.entity.ReadStatusEntity;

public interface ReadStatusRepository {
    ReadStatusEntity save(ReadStatusEntity readStatus);
}
