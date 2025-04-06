package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Specifications.VehicleSpecification;
import com.radical.be_radicalcare.ViewModels.FilterGetVm;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final IVehicleRepository vehicleRepository;

    public List<FilterGetVm> filterVehicles(
            List<String> segments, List<String> colors, Boolean sold,
            List<Integer> categoryIds, Double minCost, Double maxCost) {

        Specification<Vehicle> spec = Specification
                .where(VehicleSpecification.hasSegmentIn(segments))
                .and(VehicleSpecification.hasColorIn(colors))
                .and(VehicleSpecification.isSold(sold))
                .and(VehicleSpecification.hasCategoryIdIn(categoryIds))
                .and(VehicleSpecification.hasCostBetween(minCost, maxCost));

        List<Vehicle> vehicles = vehicleRepository.findAll(spec);

        return vehicles.stream()
                .map(FilterGetVm::from)
                .collect(Collectors.toList());
    }

    // Trả về các segment duy nhất từ danh sách các phương tiện
    public Set<String> getUniqueSegments() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        return allVehicles.stream()
                .map(Vehicle::getSegment)
                .filter(segment -> segment != null && !segment.isEmpty())
                .collect(Collectors.toSet());
    }

    // Trả về các màu duy nhất từ danh sách các phương tiện
    public Set<String> getUniqueColors() {
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        return allVehicles.stream()
                .map(Vehicle::getColor)
                .filter(color -> color != null && !color.isEmpty())
                .collect(Collectors.toSet());
    }
}

