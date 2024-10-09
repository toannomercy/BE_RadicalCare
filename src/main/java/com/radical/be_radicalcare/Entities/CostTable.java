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
    @Column(name = "cost_id")
    private Long costId;
    @Column(name = "date_created")
    private LocalDate dateCreated;
    @Column(name = "base_cost")
    private Double baseCost;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "costId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Vehicle> vehicles;
    @OneToMany(mappedBy = "costId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<MotorService> motorServices;
    @OneToMany(mappedBy = "costTable", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Product> products;
    public double getPrice() {
        return baseCost;
    }

}
