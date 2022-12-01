package com.example.repository;

import com.example.entity.CarEntity;
import org.springframework.data.repository.CrudRepository;

public interface CarRepository extends CrudRepository<CarEntity,Integer> {

}
