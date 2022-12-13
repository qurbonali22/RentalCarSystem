package com.example.dto;

import com.example.enums.CarStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CarDTO {

    private Integer id;
    private String detail;
    private Long price;

    private CarStatus status;

    private LocalDateTime createdDate;

    public CarDTO(Integer id, String detail, Long price) {
        this.id = id;
        this.detail = detail;
        this.price = price;
    }

    public CarDTO() {
    }

    @Override
    public String toString() {
        return "{" +
                " detail='" + detail + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", createdDate=" + createdDate +
                '}';
    }
}
