package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(name = "category_name", length = 50, nullable = false)
    private String categoryName;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @OneToMany(mappedBy = "categoryId", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Vehicle> vehicles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warranty_info_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @ToString.Exclude
    private WarrantyInfo warrantyInfo;
}
