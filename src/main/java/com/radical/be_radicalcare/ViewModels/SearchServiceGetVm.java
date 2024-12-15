//package com.radical.be_radicalcare.ViewModels;
//
//import com.radical.be_radicalcare.Entities.MotorService;
//import lombok.Builder;
//
//@Builder
//public record SearchServiceGetVm(
//        String serviceName,         // Tên dịch vụ
//        String serviceDescription,  // Mô tả dịch vụ
//        String category,            // Danh mục dịch vụ từ Category
//        Double price                // Giá dịch vụ từ CostTable
//) {
//    // Ánh xạ từ MotorService entity
//    public static SearchServiceGetVm fromMotorServiceEntity(MotorService motorService) {
//        return SearchServiceGetVm.builder()
//                .serviceName(motorService.getServiceName()) // Tên dịch vụ
//                .serviceDescription(motorService.getServiceDescription()) // Mô tả dịch vụ
//                .category(motorService.getCategoryId() != null ? motorService.getCategoryId().getCategoryName() : null)
//                .price(motorService.getCostId() != null ? motorService.getCostId().getBaseCost() : null) // Giá dịch vụ từ CostTable
//                .build();
//    }
//}
