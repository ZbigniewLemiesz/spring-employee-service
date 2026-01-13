package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */
public interface EmployeeService {
    Page<EmployeeResponseDto> findAll(String firstName, String lastName, String email, Pageable pageable);

    EmployeeResponseDto findById(Long id);

    EmployeeResponseDto create(EmployeeCreateDto dto);

    EmployeeResponseDto update(Long id, EmployeePutDto dto);

    EmployeeResponseDto patchDto(Long id, EmployeePatchDto employeePatchDto);

    void delete(Long id, Long verson);
}

