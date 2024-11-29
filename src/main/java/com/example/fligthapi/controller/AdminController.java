package com.example.fligthapi.controller;
import com.example.fligthapi.entity.Flight;
import com.example.fligthapi.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1")
public class AdminController {

    private final FlightService flightService;

    @PostMapping("/flight")
    public ResponseEntity<?> addFlight(@RequestBody Flight flight) {
        String status = flightService.addFlight(flight);
        if ("successful".equals(status)) {
            return ResponseEntity.ok().body("{\"status\": \"successful\"}");
        } else {
            return ResponseEntity.badRequest().body("{\"status\": \"error\", \"message\": \"" + status + "\"}");
        }
    }
    @GetMapping("/report")
    public ResponseEntity<List<Flight>> getAllFlights(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            List<Flight> flights = flightService.getAllFlights(PageRequest.of(page, size)).getContent();
            return ResponseEntity.ok(flights);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}

