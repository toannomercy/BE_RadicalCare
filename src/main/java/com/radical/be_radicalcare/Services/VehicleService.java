package com.radical.be_radicalcare.Services;

import com.radical.be_radicalcare.Entities.*;
import com.radical.be_radicalcare.Repositories.IVehicleImageRepository;
import com.radical.be_radicalcare.Repositories.IVehicleRepository;
import com.radical.be_radicalcare.ViewModels.VehiclePostVm;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})
@RequiredArgsConstructor
public class VehicleService {
    private final IVehicleRepository vehicleRepository;
    private final IVehicleImageRepository vehicleImageRepository;
    private final CloudinaryService cloudinaryService;
    private final SupplierService supplierService;
    private final CostTableService costTableService;
    private final CategoryService categoryService;

    public Page<Vehicle> getAllVehicles(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return vehicleRepository.findAll(pageable);
    }

    public Optional<Vehicle> getVehicleById(String chassisNumber) {
        return vehicleRepository.findById(chassisNumber);
    }

    public void addVehicle(Vehicle vehicle, List<MultipartFile> images) throws IOException {
        if (vehicle.getSupplierId() != null && vehicle.getSupplierId().getSupplierId() != null) {
            Supplier supplier = supplierService.getSupplierById(vehicle.getSupplierId().getSupplierId()).orElse(null);
            vehicle.setSupplierId(supplier);
        }
        if (vehicle.getCostId() != null && vehicle.getCostId().getCostId() != null) {
            CostTable costTable = costTableService.getCostTableById(vehicle.getCostId().getCostId()).orElse(null);
            vehicle.setCostId(costTable);
        }
        if (vehicle.getCategoryId() != null && vehicle.getCategoryId().getId() != null) {
            Category category = categoryService.getCategoryById(vehicle.getCategoryId().getId()).orElse(null);
            vehicle.setCategoryId(category);
        }

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        saveImages(images, savedVehicle);
    }


    public void updateVehicle(String chassisNumber, VehiclePostVm vehiclePostVm, List<MultipartFile> newImages) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(chassisNumber).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        vehicle.setChassisNumber(vehiclePostVm.chassisNumber());
        vehicle.setVehicleName(vehiclePostVm.vehicleName());
        vehicle.setImportDate(vehiclePostVm.importDate());
        vehicle.setVersion(vehiclePostVm.version());
        vehicle.setColor(vehiclePostVm.color());
        vehicle.setSegment(vehiclePostVm.segment());

        if (vehiclePostVm.costId() != null) {
            CostTable costTable = costTableService.getCostTableById(vehiclePostVm.costId()).orElse(null);
            vehicle.setCostId(costTable);
        }
        if (vehiclePostVm.categoryId() != null) {
            Category category = categoryService.getCategoryById(vehiclePostVm.categoryId()).orElse(null);
            vehicle.setCategoryId(category);
        }
        if (vehiclePostVm.supplierId() != null) {
            Supplier supplier = supplierService.getSupplierById(vehiclePostVm.supplierId()).orElse(null);
            vehicle.setSupplierId(supplier);
        }

        if (newImages != null && !newImages.isEmpty()) {
            List<VehicleImage> existingImages = vehicleImageRepository.findByVehicleId(vehicle.getChassisNumber());
            for (VehicleImage image : existingImages) {
                cloudinaryService.delete(image.getImageUrl());
                vehicleImageRepository.delete(image);
            }

            saveImages(newImages, vehicle);
        }

        vehicleRepository.save(vehicle);
    }

    private void saveImages(List<MultipartFile> newImages, Vehicle vehicle) throws IOException {
        for (MultipartFile newImage : newImages) {
            Map uploadResult = cloudinaryService.upload(newImage);
            String imageUrl = (String) uploadResult.get("url");
            VehicleImage vehicleImage = new VehicleImage();
            vehicleImage.setImageUrl(imageUrl);
            vehicleImage.setChassisNumber(vehicle);
            vehicleImageRepository.save(vehicleImage);
        }
    }

    public void deleteVehicle(String chassisNumber) {
        Vehicle vehicle = vehicleRepository.findById(chassisNumber).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));

        List<VehicleImage> images = vehicleImageRepository.findByVehicleId(vehicle.getChassisNumber());

        for (VehicleImage image : images) {
            cloudinaryService.delete(image.getImageUrl());
            vehicleImageRepository.delete(image);
        }

        vehicleRepository.delete(vehicle);
    }

}
