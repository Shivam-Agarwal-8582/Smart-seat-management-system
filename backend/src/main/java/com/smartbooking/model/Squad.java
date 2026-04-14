package com.smartbooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "squads")
public class Squad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;

    public Squad() {}

    public Squad(Long id, String name, Batch batch) {
        this.id = id;
        this.name = name;
        this.batch = batch;
    }

    public static SquadBuilder builder() {
        return new SquadBuilder();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }

    public static class SquadBuilder {
        private Long id;
        private String name;
        private Batch batch;

        public SquadBuilder id(Long id) { this.id = id; return this; }
        public SquadBuilder name(String name) { this.name = name; return this; }
        public SquadBuilder batch(Batch batch) { this.batch = batch; return this; }
        public Squad build() { return new Squad(id, name, batch); }
    }
}
