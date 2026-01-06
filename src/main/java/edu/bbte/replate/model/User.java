package edu.bbte.replate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

import java.sql.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@ToString(callSuper = true, exclude = "listings")
public class User extends BaseEntity {
    @Column(unique = true, nullable = false, length = 31)
    @NonNull
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(unique = true, nullable = false, length = 15)
    private String phoneNumber;

    @Column(nullable = false)
    private Date joinDate;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<UserRole> roles = EnumSet.noneOf(UserRole.class);

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Listing> listings;

    @PrePersist
    private void onCreate() {
        joinDate = new Date(System.currentTimeMillis());
    }
}
