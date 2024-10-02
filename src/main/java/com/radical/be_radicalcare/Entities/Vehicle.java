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
    @Column(length = 17)
    private String chassisNumber;
    @Column(name = "vehicleName", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "vehicleName must be between 1 and 50 characters")
    @NotBlank(message = "vehicleName must not be blank")
    private String vehicleName;
    @Column(name = "importDate")
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
    @Column(name = "isDeleted")
    private Boolean isDeleted;
    @Column(name = "isAvailable")
    private Boolean sold;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "costId", referencedColumnName = "costId")
    @ToString.Exclude
    private CostTable costId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", referencedColumnName = "categoryId")
    @ToString.Exclude
    private Category categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplierId", referencedColumnName = "supplierId")
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
