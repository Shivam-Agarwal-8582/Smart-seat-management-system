package com.smartbooking.service;

import com.smartbooking.model.BlockedDay;
import com.smartbooking.model.Seat;
import com.smartbooking.model.User;
import com.smartbooking.repository.BlockedDayRepository;
import com.smartbooking.repository.BookingRepository;
import com.smartbooking.repository.SeatRepository;
import com.smartbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final BlockedDayRepository blockedDayRepository;
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public AdminService(BlockedDayRepository blockedDayRepository, BookingRepository bookingRepository, 
                        SeatRepository seatRepository, UserRepository userRepository) {
        this.blockedDayRepository = blockedDayRepository;
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BlockedDay addBlockedDay(BlockedDay blockedDay) {
        bookingRepository.deleteByBookingDate(blockedDay.getDate());
        return blockedDayRepository.save(blockedDay);
    }

    public List<BlockedDay> getAllBlockedDays() {
        return blockedDayRepository.findAll();
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Seat saveSeat(Seat seat) {
        return seatRepository.save(seat);
    }

    public java.util.Map<String, Long> getDashboardStats() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        stats.put("totalSeats", seatRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        return stats;
    }
}
