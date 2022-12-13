package com.example.service;

import com.example.dto.CarDTO;
import com.example.entity.CarEntity;
import com.example.entity.ProfileEntity;
import com.example.enums.CarStatus;
import com.example.repository.CarRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    @Autowired
    private CarRepository repository;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private UnsavedProfileService unsavedProfileService;

    public void create(String detail, Long tgId){

        CarEntity entity = new CarEntity();
        ProfileEntity profile = profileService.get(tgId);

        entity.setDetail(detail);
        entity.setProfile(profile);
        entity.setCreatedDate(LocalDateTime.now());

        repository.save(entity);
        unsavedProfileService.updateCarId(tgId, entity.getId());
    }

    public Boolean updatePrice(String price, Long tgId){

         Integer carId = unsavedProfileService.getCarId(tgId);
         Optional<CarEntity> entity = repository.findById(carId);

         if (entity.isEmpty()) throw new RuntimeException("Car not found");

         CarEntity carEntity = entity.get();
         carEntity.setPrice(Long.parseLong(price));
         carEntity.setStatus(CarStatus.NOT_BUSY);
         repository.save(carEntity);
         return true;
    }

    public void updatePrice2(String price, Long tgId){

        Integer carId = unsavedProfileService.getCarId(tgId);
        Optional<CarEntity> entity = repository.findById(carId);

        if (entity.isEmpty()) throw new RuntimeException("Car not found");

        CarEntity carEntity = entity.get();
        carEntity.setPrice(Long.parseLong(price));
        repository.save(carEntity);
    }

    public void updateDetail(String detail, Long tgId){

        Integer carId = unsavedProfileService.getCarId(tgId);
        Optional<CarEntity> entity = repository.findById(carId);

        if (entity.isEmpty()) throw new RuntimeException("Car not found");

        CarEntity carEntity = entity.get();
        carEntity.setDetail(detail);
        repository.save(carEntity);
    }

    public Boolean updateStatus(Long tgId){

        Integer carId = unsavedProfileService.getCarId(tgId);
        Optional<CarEntity> entity = repository.findById(carId);

        if (entity.isEmpty()) return false;

        CarEntity carEntity = entity.get();
        if (carEntity.getStatus().equals(CarStatus.BUSY)){
            carEntity.setStatus(CarStatus.NOT_BUSY);
        } else carEntity.setStatus(CarStatus.BUSY);

        repository.save(carEntity);
        return true;
    }


    public CarEntity get(Integer id){

        Optional<CarEntity> optional = repository.findById(id);

        return optional.isEmpty()?null: optional.get();
    }



    public List<CarDTO> getAll(Integer size, Integer page, Long tgId){

        ProfileEntity profile = profileService.get(tgId);
        Pageable pageable = PageRequest.of(page, size);

        Page<CarEntity> obj = repository.getAllByProfile(profile, pageable);
        List<CarEntity> entities = obj.getContent();

        List<CarDTO> dtoList = new LinkedList<>();

        for (CarEntity entity : entities) {
            dtoList.add(toDTO(entity));
        }

        return dtoList;
    }

    public List<CarDTO> getAllByStatus(Integer size, Integer page, CarStatus status){

        Pageable pageable = PageRequest.of(page, size);

        Page<CarEntity> obj = repository.getAllByStatus(status, pageable);
        List<CarEntity> entities = obj.getContent();

        List<CarDTO> dtoList = new LinkedList<>();

        for (CarEntity entity : entities) {
            dtoList.add(toDTO(entity));
        }

        return dtoList;
    }

    CarDTO toDTO(CarEntity entity){

        CarDTO dto = new CarDTO();
        dto.setId(entity.getId());
        dto.setDetail(entity.getDetail());
        dto.setPrice(entity.getPrice());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setStatus(entity.getStatus());

        return dto;
    }
}
