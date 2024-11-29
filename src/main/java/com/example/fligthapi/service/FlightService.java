package com.example.fligthapi.service;
import com.example.fligthapi.entity.Flight;
import com.example.fligthapi.entity.Ticket;
import com.example.fligthapi.repository.FlightRepository;
import com.example.fligthapi.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final TicketRepository ticketRepository;

    public String addFlight(Flight flight) {
        try {
            flightRepository.save(flight);
            return "successful";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
    public Page<Flight> getAllFlights(Pageable pageable) {
        return flightRepository.findAll(pageable);
    }
    public List<Flight> queryAvailableFlights(String date, String from, String to,Pageable pageable) {
        List<Flight> flights = flightRepository.findByFromCityAndToCityAndAvailableDates(from,to,date, pageable).getContent();
        return flights.stream()
                .filter(flight -> flight.getCapacity() > 0)
                .collect(Collectors.toList());
    }

    @Transactional
    public String bookTicket(String date, String from, String to, String passengerFullname) {
        // Veritabanında uçuşu bul
        Flight flight = flightRepository.findByFromCityAndToCityAndAvailableDates(from, to, date)
                .stream().findFirst().orElse(null);

        if (flight == null || flight.getCapacity() <= 0) {
            throw new RuntimeException("Flight is full or does not exist");
        }

        // Kapasiteyi azalt
        flight.setCapacity(flight.getCapacity() - 1);

        // Veritabanında güncelle
        flightRepository.save(flight);

        // Benzersiz bilet numarası oluştur
        String ticketNumber = UUID.randomUUID().toString();

        // Bilet kaydı oluştur
        Ticket ticket = new Ticket();
        ticket.setTicketNumber(ticketNumber);
        ticket.setPassengerFullname(passengerFullname);
        ticket.setFlight(flight);
        ticket.setCheckedIn(false);
        ticketRepository.save(ticket);

        return ticketNumber; // Bilet numarasını döndür
    }

    public boolean checkIn(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Invalid ticket number"));

        if (ticket.isCheckedIn()) {
            throw new RuntimeException("Already checked in");
        }

        ticket.setCheckedIn(true);
        ticketRepository.save(ticket);
        return true;

    }
}
