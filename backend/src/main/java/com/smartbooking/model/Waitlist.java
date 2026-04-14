package com.smartbooking.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "waitlists")
public class Waitlist {
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
    private LocalDateTime createdAt;

    public Waitlist() {}

    public Waitlist(Long id, User user, Seat seat, LocalDate bookingDate, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.seat = seat;
        this.bookingDate = bookingDate;
        this.createdAt = createdAt;
    }

    public static WaitlistBuilder builder() {
        return new WaitlistBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Seat getSeat() { return seat; }
    public void setSeat(Seat seat) { this.seat = seat; }
    public LocalDate getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class WaitlistBuilder {
        private Long id;
        private User user;
        private Seat seat;
        private LocalDate bookingDate;
        private LocalDateTime createdAt;

        public WaitlistBuilder id(Long id) { this.id = id; return this; }
        public WaitlistBuilder user(User user) { this.user = user; return this; }
        public WaitlistBuilder seat(Seat seat) { this.seat = seat; return this; }
        public WaitlistBuilder bookingDate(LocalDate bookingDate) { this.bookingDate = bookingDate; return this; }
        public WaitlistBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Waitlist build() { return new Waitlist(id, user, seat, bookingDate, createdAt); }
    }
}
