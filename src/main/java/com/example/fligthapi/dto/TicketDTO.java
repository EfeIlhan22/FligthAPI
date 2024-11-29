package com.example.fligthapi.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketDTO {
    private Long id;
    private String passengerName;
    private Long flightId;
    private String ticketNumber;
}