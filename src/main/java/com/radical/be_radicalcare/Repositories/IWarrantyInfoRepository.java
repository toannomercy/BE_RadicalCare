package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.WarrantyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IWarrantyInfoRepository extends JpaRepository<WarrantyInfo, Long> {
}