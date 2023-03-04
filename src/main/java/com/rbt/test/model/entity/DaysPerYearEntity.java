package com.rbt.test.model.entity;

import lombok.*;

import javax.persistence.*;


@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "days_per_year")
@Table(name = "days_per_year", uniqueConstraints =
        {@UniqueConstraint(name = "UniqueYear", columnNames = {"employee_id", "year"})})
public class DaysPerYearEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @ToString.Exclude
    private EmployeeEntity employee;

    private int year;

    private int numberOfDays;
}
