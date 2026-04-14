package com.smartbooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType type;

    @OneToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    public enum SeatType { FIXED, FLOATING }

    public Seat() {}

    public Seat(Long id, String seatNumber, SeatType type, User assignedUser) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.type = type;
        this.assignedUser = assignedUser;
    }

    public static SeatBuilder builder() {
        return new SeatBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public SeatType getType() { return type; }
    public void setType(SeatType type) { this.type = type; }
    public User getAssignedUser() { return assignedUser; }
    public void setAssignedUser(User assignedUser) { this.assignedUser = assignedUser; }

    public static class SeatBuilder {
        private Long id;
        private String seatNumber;
        private SeatType type;
        private User assignedUser;

        public SeatBuilder id(Long id) { this.id = id; return this; }
        public SeatBuilder seatNumber(String seatNumber) { this.seatNumber = seatNumber; return this; }
        public SeatBuilder type(SeatType type) { this.type = type; return this; }
        public SeatBuilder assignedUser(User assignedUser) { this.assignedUser = assignedUser; return this; }
        public Seat build() { return new Seat(id, seatNumber, type, assignedUser); }
    }
}
