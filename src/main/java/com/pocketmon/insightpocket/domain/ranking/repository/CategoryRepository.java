package com.pocketmon.insightpocket.domain.ranking.repository;

import com.pocketmon.insightpocket.domain.ranking.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCode(String code);
}