package com.example.service;

import com.example.entity.UnsavedProfileEntity;
import com.example.enums.ProfileStep;
import com.example.repository.UnsavedProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UnsavedProfileService {

    @Autowired
    private UnsavedProfileRepository unsavedProfileRepository;

    public void create(Long tgId){
        UnsavedProfileEntity entity = new UnsavedProfileEntity();
        entity.setTgId(tgId);
        entity.setStep(ProfileStep.PHONE);
        unsavedProfileRepository.save(entity);
    }

    public Boolean updatePhone(String phone, Long tgId){

        UnsavedProfileEntity entity = getByTgId(tgId);
        entity.setPhone(phone);
        entity.setStep(ProfileStep.NAME);
        unsavedProfileRepository.save(entity);
        return true;

    }

    public UnsavedProfileEntity getByTgId(Long tgId){

        return unsavedProfileRepository.findByTgId(tgId).orElseThrow(() -> {
            throw new RuntimeException();
        });
    }

    public Boolean updateName(String name, Long tgId){

        UnsavedProfileEntity entity = getByTgId(tgId);
        entity.setName(name);
        entity.setStep(ProfileStep.SURNAME);
        unsavedProfileRepository.save(entity);
        return true;
    }

    public Boolean updateSurname(String surname, Long tgId){

        UnsavedProfileEntity entity = getByTgId(tgId);
        entity.setSurname(surname);
        entity.setStep(ProfileStep.DEFAULT);
        unsavedProfileRepository.save(entity);
        return true;
    }

    public Boolean updateStep(ProfileStep step, Long tgId){

        UnsavedProfileEntity entity = getByTgId(tgId);
        entity.setStep(ProfileStep.CAR_DETAIL);
        unsavedProfileRepository.save(entity);
        return true;

    }

    public ProfileStep getProfileStepByTgId(Long tgId){

        Optional<UnsavedProfileEntity> optional = unsavedProfileRepository.findByTgId(tgId);

        return optional.map(UnsavedProfileEntity::getStep).orElse(null);
    }

}
