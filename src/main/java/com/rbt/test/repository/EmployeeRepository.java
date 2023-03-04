package com.rbt.test.repository;

import com.rbt.test.model.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    Optional<EmployeeEntity> findByEmail(@NotNull String email);

    Boolean existsByEmail(@NotNull String email);
}
