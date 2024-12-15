package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "created_date")
    private Date createdDate;
    @Column(name = "total_amount")
    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ToString.Exclude
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ToString.Exclude
    private Employee employee;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<InvoiceDetail> invoiceDetails;
}
