package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.MotorService;
import com.radical.be_radicalcare.Entities.Vehicle;
import com.radical.be_radicalcare.Repositories.IMotorServicesRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.Services.CategoryService;
import com.radical.be_radicalcare.Specifications.VehicleSpecification;
import com.radical.be_radicalcare.ViewModels.SearchGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final IVehicleRepository vehicleRepository;
    private final IMotorServicesRepository motorServicesRepository; // Nếu bạn dùng motor services
    private final CategoryService categoryService;

    // Tìm kiếm Vehicle theo keyword và các tiêu chí lọc
    public Page<SearchGetVm> searchVehicles(
            Specification<Vehicle> spec,
            int page,
            int size,
            String sortBy
    ) {
        // Tìm kiếm với phân trang và sắp xếp
        Page<Vehicle> vehicles = vehicleRepository.findAll(
                spec, PageRequest.of(page, size, Sort.by(sortBy))
        );

        // Ánh xạ từ Vehicle sang SearchGetVm
        return vehicles.map(SearchGetVm::fromVehicleEntity);
    }

    // Tìm kiếm Vehicle chỉ với keyword
    public List<SearchGetVm> searchVehiclesByKeyword(String keyword) {
        Specification<Vehicle> spec = Specification.where(
                VehicleSpecification.hasKeyword(keyword, categoryService)
        );

        List<Vehicle> vehicles = vehicleRepository.findAll(spec);

        return vehicles.stream()
                .map(SearchGetVm::fromVehicleEntity)
                .toList();
    }

    // (Nếu cần) Tìm kiếm MotorService theo keyword hoặc các tiêu chí lọc khác
    public Page<SearchGetVm> searchMotorServices(
            Specification<MotorService> spec,
            int page,
            int size,
            String sortBy
    ) {
        Page<MotorService> motorServices = motorServicesRepository.findAll(
                spec, PageRequest.of(page, size, Sort.by(sortBy))
        );

        return motorServices.map(SearchGetVm::fromMotorServiceEntity);
    }
}
