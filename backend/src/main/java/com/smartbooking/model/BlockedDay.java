package com.smartbooking.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "blocked_days")
public class BlockedDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    private String description;

    public BlockedDay() {}

    public BlockedDay(Long id, LocalDate date, String description) {
        this.id = id;
        this.date = date;
        this.description = description;
    }

    public static BlockedDayBuilder builder() {
        return new BlockedDayBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static class BlockedDayBuilder {
        private Long id;
        private LocalDate date;
        private String description;

        public BlockedDayBuilder id(Long id) { this.id = id; return this; }
        public BlockedDayBuilder date(LocalDate date) { this.date = date; return this; }
        public BlockedDayBuilder description(String description) { this.description = description; return this; }
        public BlockedDay build() { return new BlockedDay(id, date, description); }
    }
}
