package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;
    @Column(name = "supplier_name", length = 50, nullable = false)
    private String supplierName;
    @Column(name = "supplier_address", nullable = false)
    private String supplierAddress;
    @Column(name = "supplier_phone", nullable = false)
    private String supplierPhone;
    @Column(name = "supplier_email",nullable = false)
    private String supplierEmail;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "supplierId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Vehicle> vehicles;
}
