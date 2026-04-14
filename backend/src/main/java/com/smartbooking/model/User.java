package com.smartbooking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "squad_id")
    private Squad squad;

    public User() {}

    public User(Long id, String name, String email, Role role, Squad squad) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.squad = squad;
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public enum Role { USER, ADMIN }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Squad getSquad() { return squad; }
    public void setSquad(Squad squad) { this.squad = squad; }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private Role role;
        private Squad squad;

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder role(Role role) { this.role = role; return this; }
        public UserBuilder squad(Squad squad) { this.squad = squad; return this; }
        public User build() { return new User(id, name, email, role, squad); }
    }
}
