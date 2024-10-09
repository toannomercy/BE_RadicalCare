package com.radical.be_radicalcare.Repositories;


import com.radical.be_radicalcare.Entities.CostTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICostTableRepository extends JpaRepository<CostTable, Long> {
}
