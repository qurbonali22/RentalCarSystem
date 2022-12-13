package com.example.service;

import com.example.entity.ProfileEntity;
import com.example.entity.UnsavedProfileEntity;
import com.example.enums.Role;
import com.example.repository.ProfileRepository;
import com.example.repository.UnsavedProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UnsavedProfileService unsavedProfileService;

    public Boolean getByTgId(Long tgId){

        return get(tgId) != null;
    }

    public void create(Long tgId){

        UnsavedProfileEntity profile = unsavedProfileService.getByTgId(tgId);

        ProfileEntity entity = new ProfileEntity();
        entity.setName(profile.getName());
        entity.setSurname(profile.getSurname());
        entity.setTgId(tgId);
        entity.setPhone(profile.getPhone());
        entity.setRole(Role.USER);
        entity.setCreatedDate(LocalDateTime.now());

        profileRepository.save(entity);
    }

    public ProfileEntity get(Long tgId){

        Optional<ProfileEntity> optional = profileRepository.findByTgId(tgId);

        return optional.isEmpty()?null:optional.get();

    }
}
