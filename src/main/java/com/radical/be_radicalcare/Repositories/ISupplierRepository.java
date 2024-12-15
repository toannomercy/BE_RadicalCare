package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISupplierRepository extends JpaRepository<Supplier, Long> {

}
