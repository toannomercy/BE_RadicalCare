package com.radical.be_radicalcare.Repositories;

import com.radical.be_radicalcare.Entities.AppointmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAppointmentDetailRepository extends JpaRepository<AppointmentDetail, Long> {
}
