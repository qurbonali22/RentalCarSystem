package com.example.service;

import com.example.entity.CarEntity;
import com.example.entity.OrderEntity;
import com.example.entity.ProfileEntity;
import com.example.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UnsavedProfileService unsavedProfileService;

    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private CarService carService;

    public void create(String date, Long tgId){

        OrderEntity entity = new OrderEntity();
        ProfileEntity profile = profileService.get(tgId);
        entity.setProfile(profile);
        entity.setStartedDate(LocalDate.parse(date));
        entity.setCreatedDate(LocalDateTime.now());

        ordersRepository.save(entity);

        unsavedProfileService.updateOrderId(tgId, entity.getId());
    }

    public Boolean updateFinishedDate(String date,Long tgId){

        OrderEntity entity = get(unsavedProfileService.getOrderId(tgId));

        entity.setFinishedDate(LocalDate.parse(date));
        ordersRepository.save(entity);
        return true;
    }

    public Boolean updateCarId(Long tgId, Integer carId){

        CarEntity car = carService.get(carId);
        OrderEntity entity = get(unsavedProfileService.getOrderId(tgId));

        entity.setCar(car);
        ordersRepository.save(entity);
        return true;

    }

    public List<OrderEntity> getAll(Integer size, Integer page, Long tgId){
        ProfileEntity profile = profileService.get(tgId);
        Pageable pageable = PageRequest.of(page, size);

        Page<OrderEntity> obj = ordersRepository.getAllByProfile(profile, pageable);

        return obj.getContent();
    }

    public OrderEntity get(Integer id){

        Optional<OrderEntity> optional = ordersRepository.findById(id);

        if (optional.isEmpty()) throw new RuntimeException("Order not found");

        return optional.get();
    }
}
