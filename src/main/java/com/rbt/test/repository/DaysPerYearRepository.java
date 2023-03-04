package com.rbt.test.repository;

import com.rbt.test.model.entity.DaysPerYearEntity;
import com.rbt.test.model.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DaysPerYearRepository extends JpaRepository<DaysPerYearEntity, Long> {

    Boolean existsByEmployeeAndYear(EmployeeEntity employee, int year);
}
