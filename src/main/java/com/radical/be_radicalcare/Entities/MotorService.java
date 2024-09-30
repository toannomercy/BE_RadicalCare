package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "motor_service")
public class MotorService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceId")
    private Long serviceId;
    @Column(name = "serviceName", length = 50, nullable = false)
    private String serviceName;
    @Column(name = "serviceDescription", nullable = false)
    private String serviceDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "costId", referencedColumnName = "costId")
    @ToString.Exclude
    private CostTable costId;
    @Column(name = "isDeleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "motorService", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<AppointmentDetail> appointmentDetails;
}
