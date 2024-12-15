package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IVehicleRepository extends
        JpaRepository<Vehicle, String>,
        PagingAndSortingRepository<Vehicle, String>,
        JpaSpecificationExecutor<Vehicle> {
    @Query("SELECT v FROM Vehicle v " +
            "LEFT JOIN FETCH v.categoryId " +
            "LEFT JOIN FETCH v.costId")
    List<Vehicle> findAllWithRelations();

    @Query("SELECT v FROM Vehicle v " +
            "LEFT JOIN FETCH v.categoryId " +
            "LEFT JOIN FETCH v.costId " +
            "WHERE v.chassisNumber = :chassisNumber")
    Optional<Vehicle> findByIdWithRelations(@Param("chassisNumber") String chassisNumber);

    @Query("SELECT v FROM Vehicle v " +
            "LEFT JOIN FETCH v.categoryId " +
            "LEFT JOIN FETCH v.costId " +
            "WHERE (:keyword IS NULL OR v.vehicleName LIKE %:keyword%)")
    Page<Vehicle> findAllWithRelations(@Param("keyword") String keyword, Pageable pageable);
}

