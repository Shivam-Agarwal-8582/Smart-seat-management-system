package com.smartbooking.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "bookingDate"}),
    @UniqueConstraint(columnNames = {"seat_id", "bookingDate"})
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Column(nullable = false)
    private LocalDate bookingDate;

    @Column(nullable = false)
    private LocalDate createdAt;

    public Booking() {}

    public Booking(Long id, User user, Seat seat, LocalDate bookingDate, LocalDate createdAt) {
        this.id = id;
        this.user = user;
        this.seat = seat;
        this.bookingDate = bookingDate;
        this.createdAt = createdAt;
    }

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public static class BookingBuilder {
        private Long id;
        private User user;
        private Seat seat;
        private LocalDate bookingDate;
        private LocalDate createdAt;

        public BookingBuilder id(Long id) { this.id = id; return this; }
        public BookingBuilder user(User user) { this.user = user; return this; }
        public BookingBuilder seat(Seat seat) { this.seat = seat; return this; }
        public BookingBuilder bookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; return this; }
        public BookingBuilder createdAt(LocalDate createdAt) { this.createdAt = createdAt; return this; }
        public Booking build() { return new Booking(id, user, seat, bookingDate, createdAt); }
    }
}
