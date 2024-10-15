package com.radical.be_radicalcare.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @Column(name= "chassis_number",length = 17)
    private String chassisNumber;
    @Column(name = "vehicle_name", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "Vehicle Name must be between 1 and 50 characters")
    @NotBlank(message = "Vehicle Name must not be blank")
    private String vehicleName;
    @Column(name = "import_date")
    private LocalDate importDate;
    @Column(name = "version", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "version must be between 1 and 50 characters")
    @NotBlank(message = "version must not be blank")
    private String version;
    @Column(name = "color", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "color must be between 1 and 50 characters")
    @NotBlank(message = "color must not be blank")
    private String color;
    @Column(name = "segment", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "segment must be between 1 and 50 characters")
    @NotBlank(message = "segment must not be blank")
    private String segment;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Column(name = "sold")
    private Boolean sold;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cost_id", referencedColumnName = "cost_id")
    @ToString.Exclude
    private CostTable costId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @ToString.Exclude
    private Category categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", referencedColumnName = "supplier_id")
    @ToString.Exclude
    private Supplier supplierId;
    @OneToMany(mappedBy = "chassisNumber", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<WarrantyHistory> warrantyHistory;
    @OneToMany(mappedBy = "chassisNumber", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<VehicleImage> vehicleImages;
    @JsonIgnore
    public List<String> getImageUrls() {
        return vehicleImages.stream().map(VehicleImage::getImageUrl).collect(Collectors.toList());
    }
}
