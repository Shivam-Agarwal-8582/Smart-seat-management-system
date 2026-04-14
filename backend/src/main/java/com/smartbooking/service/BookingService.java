package com.smartbooking.service;

import com.smartbooking.exception.ApiException;
import com.smartbooking.model.Booking;
import com.smartbooking.model.BlockedDay;
import com.smartbooking.model.User;
import com.smartbooking.model.Seat;
import com.smartbooking.model.Waitlist;
import com.smartbooking.model.Batch;
import com.smartbooking.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BlockedDayRepository blockedDayRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final WaitlistRepository waitlistRepository;

    public BookingService(BookingRepository bookingRepository, BlockedDayRepository blockedDayRepository, 
                          UserRepository userRepository, SeatRepository seatRepository, WaitlistRepository waitlistRepository) {
        this.bookingRepository = bookingRepository;
        this.blockedDayRepository = blockedDayRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
        this.waitlistRepository = waitlistRepository;
    }

    @Transactional
    public Booking bookSeat(Long userId, Long seatId, LocalDate bookingDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));
        
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ApiException("Seat not found"));

        validateBookingRules(user, seat, bookingDate);

        // Check if seat is already booked (Extra safety before DB constraint)
        if (bookingRepository.findBySeatAndBookingDate(seat, bookingDate).isPresent()) {
            throw new ApiException("This seat was just taken! Please try another one.");
        }

        Booking booking = Booking.builder()
                .user(user)
                .seat(seat)
                .bookingDate(bookingDate)
                .createdAt(LocalDate.now())
                .build();

        return bookingRepository.save(booking);
    }

    @Transactional
    public Waitlist addToWaitlist(Long userId, Long seatId, LocalDate bookingDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ApiException("Seat not found"));

        // Basic validations
        if (bookingRepository.findByUserAndBookingDate(user, bookingDate).isPresent()) {
            throw new ApiException("You already have a booking for this date.");
        }
        if (waitlistRepository.findByUserAndSeatAndBookingDate(user, seat, bookingDate).isPresent()) {
            throw new ApiException("You are already on the waitlist for this seat/date.");
        }

        Waitlist waitlist = Waitlist.builder()
                .user(user)
                .seat(seat)
                .bookingDate(bookingDate)
                .createdAt(LocalDateTime.now())
                .build();

        return waitlistRepository.save(waitlist);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException("Booking not found"));

        LocalDate date = booking.getBookingDate();
        Seat seat = booking.getSeat();

        // Delete the current booking
        bookingRepository.delete(booking);

        // Look for the next person in the waitlist for this seat and date
        List<Waitlist> queue = waitlistRepository.findBySeatAndBookingDateOrderByCreatedAtAsc(seat, date);
        if (!queue.isEmpty()) {
            Waitlist firstInQueue = queue.get(0);
            
            // Create a new booking for the waitlist person
            Booking newBooking = Booking.builder()
                    .user(firstInQueue.getUser())
                    .seat(seat)
                    .bookingDate(date)
                    .createdAt(LocalDate.now())
                    .build();
            
            bookingRepository.save(newBooking);
            
            // Remove from waitlist
            waitlistRepository.delete(firstInQueue);
            System.out.println("Waitlist automated: Seat " + seat.getSeatNumber() + " assigned to " + firstInQueue.getUser().getName());
        }
    }

    private void validateBookingRules(User user, Seat seat, LocalDate bookingDate) {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        // 1. STRICT DATE RULE: Only Tomorrow is allowed
        if (!bookingDate.equals(tomorrow)) {
            throw new ApiException("Strict Rule: You can ONLY book for tomorrow (" + tomorrow + "). Today or future dates are blocked.");
        }

        // 2. 3 PM RULE: Booking for tomorrow opens only after 3 PM today
        // DEV MODE: Overriding 3 PM check for testing purposes
        if (false) {
            throw new ApiException("Booking Window: Reservations for tomorrow open today at 3:00 PM.");
        }

        // 3. Batch schedule check
        if (user.getSquad() != null && user.getSquad().getBatch() != null) {
            Batch batch = user.getSquad().getBatch();
            if (!batch.getAllowedDays().contains(bookingDate.getDayOfWeek())) {
                throw new ApiException("Batch Restriction: Your batch (" + batch.getName() + ") is NOT allowed in office on " + bookingDate.getDayOfWeek());
            }
        }

        // 4. Holiday check
        if (blockedDayRepository.findByDate(bookingDate).isPresent()) {
            throw new ApiException("Office is CLOSED on " + bookingDate + " (Official Holiday).");
        }

        // 5. Seat Ownership
        if (seat.getType() == Seat.SeatType.FIXED) {
            if (seat.getAssignedUser() == null || !seat.getAssignedUser().getId().equals(user.getId())) {
                throw new ApiException("RESTRICTED: This is a Fixed Seat reserved for someone else.");
            }
        }

        // 6. Double booking prevention
        if (bookingRepository.findByUserAndBookingDate(user, bookingDate).isPresent()) {
            throw new ApiException("You already have an active booking for " + bookingDate);
        }
    }

    public List<Booking> getWeeklyBookings(LocalDate start) {
        return bookingRepository.findByBookingDateBetween(start, start.plusDays(6));
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByBookingDateBetween(LocalDate.now(), LocalDate.now().plusDays(30))
                .stream().filter(b -> b.getUser().getId().equals(userId)).toList();
    }

    public List<Booking> getDailyBookings(LocalDate date) {
        return bookingRepository.findByBookingDateBetween(date, date);
    }
}
