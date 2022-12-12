package com.example.dto;

import com.example.enums.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class ProfileDTO {

    private Integer id;

    private Long tgId;

    private String phone;

    private String name;

    private String surname;

    private Role role;

    private LocalDateTime createdDate;

}
