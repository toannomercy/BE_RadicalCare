package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.radical.be_radicalcare.Dto.VehicleDto;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "cart_item")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Sử dụng UUID để đảm bảo ID là duy nhất
    @Column(name = "id", nullable = false)
    private String id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "vehicle_id", nullable = false)
//    private Vehicle vehicle;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Transient // Không lưu trong database, chỉ lưu trong Redis
    @JsonSerialize
    @JsonDeserialize
    private VehicleDto vehicleDto;

}
