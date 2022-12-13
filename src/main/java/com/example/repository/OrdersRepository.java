package com.example.repository;

import com.example.entity.OrderEntity;
import com.example.entity.ProfileEntity;
import javassist.util.HotSwapAgent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface OrdersRepository extends CrudRepository<OrderEntity, Integer> {

    Page<OrderEntity> getAllByProfile(ProfileEntity profile, Pageable pageable);


}
