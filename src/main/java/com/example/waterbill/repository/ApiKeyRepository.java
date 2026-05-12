package com.example.waterbill.repository;

import com.example.waterbill.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    // This will find the key only if it's active
    Optional<ApiKey> findByKeyValueAndActiveTrue(String keyValue);
}