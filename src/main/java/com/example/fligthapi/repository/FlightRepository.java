package com.example.fligthapi.repository;
import com.example.fligthapi.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    List<Flight> findByFromCityAndToCityAndAvailableDates(String from, String to, String date);
    Page<Flight> findByFromCityAndToCityAndAvailableDates(String from, String to, String date, Pageable pageable);
}
