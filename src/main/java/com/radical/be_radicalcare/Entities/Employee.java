package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "employee")
public class Employee{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "date_of_birth")
    private LocalDate dob;
    @Column(name = "position")
    private String position;
    @Column(name = "address")
    private String address;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Contract> contracts;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ToString.Exclude
    private User userId;
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Invoice> invoices;
}
