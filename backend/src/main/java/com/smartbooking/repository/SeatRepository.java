package com.smartbooking.repository;
import com.smartbooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
public interface SeatRepository extends JpaRepository<Seat, Long> {}
