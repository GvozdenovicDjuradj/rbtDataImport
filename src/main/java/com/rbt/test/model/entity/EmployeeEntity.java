package com.rbt.test.model.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "employee")
@Table(name = "employee")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Email
    @Column(unique=true)
    private String email;
    @NotNull
    private String password;

    private int totalVacationDaysLeft;
    private int totalVacationDaysUsed;


}
