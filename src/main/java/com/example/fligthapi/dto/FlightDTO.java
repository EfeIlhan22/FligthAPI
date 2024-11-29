package com.example.fligthapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FlightDTO {
    private Long id;
    private String from;
    private String to;
    private List<String> availableDates;
    private int availableSeats;

    // Getters ve Setters
}
