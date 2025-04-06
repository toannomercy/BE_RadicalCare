package com.radical.be_radicalcare.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    @JsonProperty("chassisNumber")
    private String chassisNumber;

    @JsonProperty("vehicleName")
    private String vehicleName;

    @JsonProperty("importDate")
    private LocalDate importDate;

    @JsonProperty("version")
    private String version;

    @JsonProperty("color")
    private String color;

    @JsonProperty("segment")
    private String segment;

    @JsonProperty("isDeleted")
    private Boolean isDeleted;

    @JsonProperty("sold")
    private Boolean sold;

    @JsonProperty("price")
    private Double price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("imageUrls")
    private List<String> imageUrls;
}
