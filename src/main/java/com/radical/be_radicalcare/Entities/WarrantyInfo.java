package com.radical.be_radicalcare.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "warranty_info")
public class WarrantyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "warranty_type")
    private String warrantyType;
    @Column(name = "warranty_start_date")
    private Date warrantyStartDate;
    @Column(name = "warranty_end_date")
    private Date warrantyEndDate;
    @Column(name = "warranty_description")
    private String warrantyDescription;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", referencedColumnName = "id")
    @ToString.Exclude
    private Category category;
}
