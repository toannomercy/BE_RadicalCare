package com.radical.be_radicalcare.Entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "warranty_history")
public class WarrantyHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String warrantyType;
    private String warrantyDescription;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chassis_number", referencedColumnName = "chassis_number")
    @ToString.Exclude
    private Vehicle chassisNumber;
}
