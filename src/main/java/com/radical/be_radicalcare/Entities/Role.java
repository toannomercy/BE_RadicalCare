package com.radical.be_radicalcare.Entities;

import com.radical.be_radicalcare.Constants.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType name;

    @Size(max = 250, message = "Description must be less than 250 characters")
    @Column(name = "description", length = 250)
    private String description;

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    public Role(RoleType roleType) {
        this.name = roleType;
    }

    @Override
    public String getAuthority() {
        return name.name();
    }
}
