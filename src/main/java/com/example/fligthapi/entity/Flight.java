package com.example.fligthapi.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromCity;
    private String toCity;

    @ElementCollection
    private List<String> availableDates;

    @ElementCollection
    private List<String> days;

    private int capacity;
}

