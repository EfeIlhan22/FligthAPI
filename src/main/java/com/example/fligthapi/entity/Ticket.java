package com.example.fligthapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Benzersiz bilet ID

    @Column(nullable = false, unique = true)
    private String ticketNumber; // Benzersiz bilet numarası

    @Column(nullable = false)
    private String passengerFullname; // Yolcu adı

    @ManyToOne
    @JoinColumn(name = "flight_id", nullable = false)
    private Flight flight; // İlişkili uçuş

    private boolean checkedIn; // Check-in durumu

}

