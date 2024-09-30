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
@Table(name = "cost_table")
public class CostTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "costId")
    private Long costId;
    @Column(name = "dateCreated")
    private LocalDate dateCreated;
    @Column(name = "baseCost")
    private Double baseCost;
    @Column(name = "isDeleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "costId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Product> products;
    @OneToMany(mappedBy = "costId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<MotorService> motorServices;
    public double getPrice() {
        return baseCost;
    }
}
