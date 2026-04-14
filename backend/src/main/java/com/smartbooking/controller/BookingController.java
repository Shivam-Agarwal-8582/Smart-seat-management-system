package com.smartbooking.controller;
import com.smartbooking.model.Booking;
import com.smartbooking.service.BookingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    @PostMapping
    public Booking bookSeat(@RequestParam Long userId, @RequestParam Long seatId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.bookSeat(userId, seatId, date);
    }
    @PostMapping("/cancel/{id}")
    public void cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }

    @PostMapping("/waitlist")
    public void addToWaitlist(@RequestParam Long userId, @RequestParam Long seatId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        bookingService.addToWaitlist(userId, seatId, date);
    }

    @GetMapping("/my-bookings")
    public List<Booking> getMyBookings(@RequestParam Long userId) {
        return bookingService.getUserBookings(userId);
    }

    @GetMapping("/daily")
    public List<Booking> getDailyBookings(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.getDailyBookings(date);
    }
}
