package com.smartbooking.repository;
import com.smartbooking.model.BlockedDay;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
public interface BlockedDayRepository extends JpaRepository<BlockedDay, Long> {
    Optional<BlockedDay> findByDate(LocalDate date);
}
