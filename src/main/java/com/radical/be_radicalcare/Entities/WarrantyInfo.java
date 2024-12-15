package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(name = "warranty_description")
    private String warrantyDescription;

    @OneToMany(mappedBy = "warrantyInfo", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @ToString.Exclude
    @JsonIgnore
    private List<Category> category;
}
