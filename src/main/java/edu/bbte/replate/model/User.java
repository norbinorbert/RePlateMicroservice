package edu.bbte.replate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 31)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(unique = true, nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private Date joinDate;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Listing> listings;

    @PrePersist
    private void onCreate() {
        joinDate = new Date(System.currentTimeMillis());
    }
}
