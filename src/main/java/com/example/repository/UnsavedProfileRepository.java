package com.example.repository;

import com.example.entity.UnsavedProfileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UnsavedProfileRepository extends CrudRepository<UnsavedProfileEntity, Integer> {

    Optional<UnsavedProfileEntity> findByTgId(Long tgId);
}
