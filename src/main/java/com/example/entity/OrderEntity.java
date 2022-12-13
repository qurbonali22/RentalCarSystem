package com.example.entity;

import com.example.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "started_date")
    private LocalDate startedDate;

    @Column(name = "finished_date")
    private LocalDate finishedDate;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;


    @ManyToOne
    @JoinColumn(name = "car_id")
    private CarEntity car;

    @Column
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Override
    public String toString() {
        return "Order{" +
                " startedDate=" + startedDate +
                ", finishedDate=" + finishedDate +
                ", car=" + car +
                ", status=" + status +
                ", createdDate=" + createdDate +
                '}';
    }
}
