package com.rbt.test.service;

import com.rbt.test.error.RbtException;
import com.rbt.test.error.ResourceNotFoundException;
import com.rbt.test.model.dto.ImportResponse;
import com.rbt.test.model.entity.DaysPerYearEntity;
import com.rbt.test.model.entity.EmployeeEntity;
import com.rbt.test.model.entity.UsedDaysEntity;
import com.rbt.test.repository.DaysPerYearRepository;
import com.rbt.test.repository.EmployeeRepository;
import com.rbt.test.repository.UsedDaysRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static java.util.Collections.emptyList;

@Service
@AllArgsConstructor
public class DataImportService {
    private EmployeeRepository employeeRepository;
    private DaysPerYearRepository daysPerYearRepository;
    private UsedDaysRepository usedDaysRepository;
    private CsvParser csvParser;
    private final PasswordEncoder passwordEncoder;


    public ImportResponse importProfiles(MultipartFile file) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            reader.readLine();
            String line = reader.readLine();
            var array = line.split(",");
            if (array.length != 2 || !Objects.equals(array[0], "Employee Email") || !Objects.equals(array[1], "Employee Password")) {
                throw new RbtException(400, "Invalid csv format");
            }

            var employees = csvParser.parse(file, this::parseEmployee, 2);
            for (EmployeeEntity employee : employees) {
                if (!employeeRepository.existsByEmail(employee.getEmail())) {
                    employeeRepository.save(employee);
                }
            }
            return new ImportResponse(200, "Successfully stored employees", new ArrayList<>(emptyList()));
        } catch (RbtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RbtException(500, ex.getMessage());
        }


    }

    public ImportResponse importTotalNumberOfDays(MultipartFile file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            var yearHeader = reader.readLine().split(",");
            String line = reader.readLine();
            var array = line.split(",");
            if (yearHeader.length != 2 || array.length != 2 || !Objects.equals(array[0], "Employee") || !Objects.equals(array[1], "Total vacation days")) {
                throw new RbtException(400, "Invalid csv format");
            }
            var year = Integer.parseInt(yearHeader[1]);

            var daysPerYear = csvParser.parse(file, (s) -> parseDaysPerYear(s, year), 2);
            for (DaysPerYearEntity day : daysPerYear) {
                var employee = day.getEmployee();
                if (!daysPerYearRepository.existsByEmployeeAndYear(employee, day.getYear())) {
                    var currentDays = employee.getTotalVacationDaysLeft();
                    employee.setTotalVacationDaysLeft(currentDays + day.getNumberOfDays());
                    employeeRepository.save(employee);

                    daysPerYearRepository.save(day);
                }
            }
            return new ImportResponse(200, "Successfully stored total vacation days", new ArrayList<>(emptyList()));

        } catch (RbtException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new RbtException(400, "invalid file", ex);
        } catch (Exception ex) {
            throw new RbtException(500, ex.getMessage());
        }

    }

    public ImportResponse importUsedDays(MultipartFile file) {
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            String line = reader.readLine();
            var array = line.split(",");
            if (array.length != 3 || !Objects.equals(array[0], "Employee") || !Objects.equals(array[1], "Vacation start date") || !Objects.equals(array[2], "Vacation end date")) {
                throw new RbtException(400, "Invalid csv format");
            }

            var usedDays = csvParser.parse(file, (s) -> {
                try {
                    return parseUsedDays(s);
                } catch (ParseException ex) {
                    throw new RbtException(400, "invalid date format", ex);
                } catch (IOException ex) {
                    throw new RbtException(400, "invalid file", ex);
                }
            }, 1);
            for (UsedDaysEntity day : usedDays) {
                var employee = day.getEmployee();
                if (!usedDaysRepository.existsByEmployeeAndDateFromAndDateTo(employee, day.getDateFrom(), day.getDateTo())) {
                    var newDaysUsed = day.getWorkDaysCount();
                    var currentDaysUsed = employee.getTotalVacationDaysUsed();
                    var currentDaysLeft = employee.getTotalVacationDaysLeft();
                    var newDaysLeft = currentDaysLeft - newDaysUsed;
                    if (newDaysLeft < 0) {
                        continue;
                    }
                    employee.setTotalVacationDaysUsed(currentDaysUsed + newDaysUsed);
                    employee.setTotalVacationDaysLeft(newDaysLeft);
                    employeeRepository.save(employee);
                    usedDaysRepository.save(day);
                }
            }
            return new ImportResponse(200, "Successfully stored used vacation days", new ArrayList<>(emptyList()));

        } catch (RbtException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new RbtException(500, ex.getMessage());
        }
    }

    private EmployeeEntity parseEmployee(String line) {
        var array = line.split(",");
        if (array.length == 2) {
            return new EmployeeEntity(
                    null,
                    array[0],
                    passwordEncoder.encode(array[1]),
                    0,
                    0);
        } else {
            throw new RbtException(400, "invalid_csv_format");
        }
    }

    private DaysPerYearEntity parseDaysPerYear(String line, int year) {
        var array = line.split(",");
        if (array.length == 2) {
            var employee = employeeRepository.findByEmail(array[0]).orElseThrow(() -> new ResourceNotFoundException("requested employee does not exist"));
            var numberOfDays = Integer.parseInt(array[1]);
            if (numberOfDays < 0 || numberOfDays > 35) {
                throw new RbtException(400, "invalid_number_of_days");
            }
            return new DaysPerYearEntity(
                    null,
                    employee,
                    year,
                    numberOfDays);
        } else {
            throw new RbtException(400, "invalid_csv_format");
        }
    }

    private UsedDaysEntity parseUsedDays(String line) throws ParseException, IOException {
        var array = line.split("\"");
        if (array.length == 4) {
            var employeeMail = array[0].substring(0, array[0].length() - 1);
            var employee = employeeRepository.findByEmail(employeeMail).orElseThrow(() -> new ResourceNotFoundException("requested employee does not exist"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH);
            var dateFrom = LocalDate.parse(array[1], formatter);
            var dateTo = LocalDate.parse(array[3], formatter);
            return new UsedDaysEntity(
                    null,
                    employee,
                    dateFrom,
                    dateTo,
                    countWeekdays(dateFrom, dateTo));
        } else {
            throw new RbtException(400, "invalid_csv_format");
        }
    }

    public static int countWeekdays(LocalDate start, LocalDate end) {
        if (start.equals(end)) {
            if (start.getDayOfWeek() != DayOfWeek.SATURDAY && start.getDayOfWeek() != DayOfWeek.SUNDAY) {
                return 1;
            } else {
                return 0;
            }
        }
        int weekdays = 0;
        LocalDate date = start;
        while (!date.isAfter(end)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                weekdays++;
            }
            date = date.plusDays(1);
        }
        return weekdays;
    }
}
