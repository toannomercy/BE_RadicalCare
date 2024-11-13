package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Repositories.IMotorServicesRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.Specifications.MotorServicesSpecification;
import com.radical.be_radicalcare.Specifications.VehicleSpecification;
import com.radical.be_radicalcare.ViewModels.FilterGetVm;
import com.radical.be_radicalcare.ViewModels.SearchGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final IVehicleRepository vehicleRepository;
    private final CategoryService categoryService;

    public List<SearchGetVm> searchVehiclesByKeyword(String keyword) {
        // Sử dụng Specification với điều kiện tìm kiếm
        Specification<Vehicle> spec = Specification.where(
                VehicleSpecification.hasKeyword(keyword, categoryService)
        );

        List<Vehicle> vehicles = vehicleRepository.findAll(spec);

        if (vehicles.isEmpty()) {
            // Trả về danh sách rỗng nếu không tìm thấy
            return List.of();
        }

        // Ánh xạ kết quả trả về thành SearchVehicleVm
        return vehicles.stream()
                .map(SearchGetVm::fromEntity)
                .toList();
    }
}



