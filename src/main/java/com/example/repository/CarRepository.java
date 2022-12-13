package com.example.repository;

import com.example.dto.CarDTO;
import com.example.entity.CarEntity;
import com.example.entity.ProfileEntity;
import com.example.enums.CarStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CarRepository extends CrudRepository<CarEntity, Integer>,
                                        PagingAndSortingRepository<CarEntity, Integer> {

    Page<CarEntity> getAllByProfile(ProfileEntity profile, Pageable pageable);

    Page<CarEntity> getAllByStatus(CarStatus status, Pageable pageable);

}
