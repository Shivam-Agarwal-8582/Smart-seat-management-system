package com.smartbooking.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.util.Set;

@Entity
@Table(name = "batches")
public class Batch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection(targetClass = DayOfWeek.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "batch_schedule", joinColumns = @JoinColumn(name = "batch_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week")
    private Set<DayOfWeek> allowedDays;

    public Batch() {}

    public Batch(Long id, String name, Set<DayOfWeek> allowedDays) {
        this.id = id;
        this.name = name;
        this.allowedDays = allowedDays;
    }

    public static BatchBuilder builder() {
        return new BatchBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<DayOfWeek> getAllowedDays() { return allowedDays; }
    public void setAllowedDays(Set<DayOfWeek> allowedDays) { this.allowedDays = allowedDays; }

    public static class BatchBuilder {
        private Long id;
        private String name;
        private Set<DayOfWeek> allowedDays;

        public BatchBuilder id(Long id) { this.id = id; return this; }
        public BatchBuilder name(String name) { this.name = name; return this; }
        public BatchBuilder allowedDays(Set<DayOfWeek> allowedDays) { this.allowedDays = allowedDays; return this; }
        public Batch build() { return new Batch(id, name, allowedDays); }
    }
}
