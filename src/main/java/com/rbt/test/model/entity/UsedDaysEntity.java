package com.rbt.test.model.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "used_days")
@Table(name = "used_days", uniqueConstraints =
        {@UniqueConstraint(name = "UniqueInterval", columnNames = {"employee_id", "dateFrom", "dateTo"})})
public class UsedDaysEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @ToString.Exclude
    private EmployeeEntity employee;

    private LocalDate dateFrom;
    private LocalDate dateTo;

    private int workDaysCount;
}
