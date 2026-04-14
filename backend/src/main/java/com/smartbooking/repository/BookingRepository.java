package com.smartbooking.repository;

import com.smartbooking.model.Booking;
import com.smartbooking.model.User;
import com.smartbooking.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookingDateBetween(LocalDate start, LocalDate end);
    Optional<Booking> findByUserAndBookingDate(User user, LocalDate date);
    Optional<Booking> findBySeatAndBookingDate(Seat seat, LocalDate date);
    void deleteByBookingDate(LocalDate date);
}
