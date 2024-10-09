package com.radical.be_radicalcare.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "contract")
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "date_created")
    private LocalDate dateCreated;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name = "down_payment")
    private Double downPayment;
    @Column(name = "status")
    private String status;
    @Column(name = "is_installment")
    private Boolean isInstallment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ToString.Exclude
    private Customer customer;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    @ToString.Exclude
    private Employee employee;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chassis_number", referencedColumnName = "chassis_number")
    @ToString.Exclude
    private Vehicle chassisNumber;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "installment_plan_id", referencedColumnName = "id")
    @ToString.Exclude
    private InstallmentPlan installmentPlan;
}
