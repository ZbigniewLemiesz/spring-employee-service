package io.github.zlemiesz.springemployeeservice.employee;

import jakarta.validation.Valid;
import tools.jackson.databind.JsonNode;

import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */
public interface EmployeeService {
    EmployeeDto create(EmployeeDto dto);

    List<EmployeeDto> findAll();

    EmployeeDto findById(Long id);

    EmployeeDto update(Long id, EmployeePutDto dto);

    void delete(Long id);

    EmployeeDto patchDto(Long id, @Valid EmployeePatchDto employeePatchDto);
}

