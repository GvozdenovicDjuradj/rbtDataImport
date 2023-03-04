package com.rbt.test.repository;

import com.rbt.test.model.entity.EmployeeEntity;
import com.rbt.test.model.entity.UsedDaysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface UsedDaysRepository extends JpaRepository<UsedDaysEntity, Long> {

    Boolean existsByEmployeeAndDateFromAndDateTo(EmployeeEntity employee, LocalDate dateFrom, LocalDate dateTo);
}
