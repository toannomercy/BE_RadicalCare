package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.ViewModels.FilterGetVm;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterService {
    private final IVehicleRepository vehicleRepository;

    public List<Vehicle> filterVehicles(
            String segment, String color, Boolean sold, Integer categoryId, Double minCost, Double maxCost) {
        List<Vehicle> allVehicles = vehicleRepository.findAll();

        // Kiểm tra nếu allVehicles là null
        if (allVehicles == null) {
            System.out.println("No vehicles found in repository.");
            allVehicles = new ArrayList<>();  // Khởi tạo danh sách trống nếu null
        }

        System.out.println("Total Vehicles before filter: " + allVehicles.size());

        return allVehicles.stream()
                .filter(vehicle -> (segment == null || vehicle.getSegment().equalsIgnoreCase(segment)))
                .filter(vehicle -> (color == null || vehicle.getColor().equalsIgnoreCase(color)))
                .filter(vehicle -> (sold == null || vehicle.getSold() == sold))
                .filter(vehicle -> {
                    boolean result = (categoryId == null ||
                            (vehicle.getCategoryId() != null && vehicle.getCategoryId().getId().intValue() == categoryId));
                    if (!result) {
                        System.out.println("Filtered out by category: " +
                                vehicle.getChassisNumber() + " | Category ID: " +
                                (vehicle.getCategoryId() != null ? vehicle.getCategoryId().getId() : "null"));
                    }
                    return result;
                })
                .filter(vehicle -> (minCost == null || vehicle.getCostId().getBaseCost() >= minCost))
                .filter(vehicle -> (maxCost == null || vehicle.getCostId().getBaseCost() <= maxCost))
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

