package com.example.fligthapi.controller;

import com.example.fligthapi.entity.Flight;
import com.example.fligthapi.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flights/v1")
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/query")
    public ResponseEntity<List<Flight>> queryFlights(
            @RequestParam String date,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            List<Flight> flights = flightService.queryAvailableFlights(date, from, to, PageRequest.of(page, size));
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/buy-ticket")
    public ResponseEntity<String> buyTicket(
            @RequestParam String date,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String passengerFullname) {
        try {
            String ticketNumber = flightService.bookTicket(date, from, to, passengerFullname);
            return ResponseEntity.ok("Status: Successful, Ticket Number: " + ticketNumber);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("Status: Error - " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Status: Error - An unexpected error occurred");
        }
    }
    @PostMapping("/check-in")
    public ResponseEntity<String> checkIn(@RequestParam String ticketNumber) {
        try {
            boolean success = flightService.checkIn(ticketNumber);
            if (success) {
                return ResponseEntity.ok("Status: Successful - Check-in completed");
            } else {
                return ResponseEntity.status(400).body("Status: Error - Invalid ticket number or already checked in");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Status: Error - An unexpected error occurred");
        }
    }
}

