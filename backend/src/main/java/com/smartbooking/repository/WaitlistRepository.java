package com.smartbooking.repository;

import com.smartbooking.model.Waitlist;
import com.smartbooking.model.Seat;
import com.smartbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WaitlistRepository extends JpaRepository<Waitlist, Long> {
    List<Waitlist> findBySeatAndBookingDateOrderByCreatedAtAsc(Seat seat, LocalDate date);
    Optional<Waitlist> findByUserAndSeatAndBookingDate(User user, Seat seat, LocalDate date);
    void deleteBySeatAndBookingDate(Seat seat, LocalDate date);
}
