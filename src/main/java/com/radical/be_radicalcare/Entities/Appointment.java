package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "appointment")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "date_created")
    private LocalDate dateCreated;
    @Column(name = "status")
    private String status="Pending";
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ToString.Exclude
    private Customer customer;
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;
    @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<AppointmentDetail> appointmentDetails;
}
