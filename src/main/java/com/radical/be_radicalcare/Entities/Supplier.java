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
    @Column(name = "supplierId")
    private Long supplierId;
    @Column(name = "supplierName", length = 50, nullable = false)
    private String supplierName;
    @Column(name = "supplierAddress", nullable = false)
    private String supplierAddress;
    @Column(name = "supplierPhone", nullable = false)
    private String supplierPhone;
    @Column(name = "supplierEmail",nullable = false)
    private String supplierEmail;
    @Column(name = "isDeleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "supplierId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Product> products;
}
