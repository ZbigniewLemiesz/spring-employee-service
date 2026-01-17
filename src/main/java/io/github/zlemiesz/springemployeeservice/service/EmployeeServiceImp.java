package io.github.zlemiesz.springemployeeservice.service;

import io.github.zlemiesz.springemployeeservice.dto.*;
import io.github.zlemiesz.springemployeeservice.exception.EmailAlreadyInUseException;
import io.github.zlemiesz.springemployeeservice.exception.EmployeeNotFoundException;
import io.github.zlemiesz.springemployeeservice.exception.VersionMismatchException;
import io.github.zlemiesz.springemployeeservice.mapper.EmployeeMapper;
import io.github.zlemiesz.springemployeeservice.model.Employee;
import io.github.zlemiesz.springemployeeservice.repository.EmployeeRepository;
import io.github.zlemiesz.springemployeeservice.specification.EmployeeSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author Zbigniew Lemiesz
 */

@Service
public class EmployeeServiceImp implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImp(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Transactional
    @Override
    public EmployeeResponseDto create(EmployeeCreateDto dto) {
        if (dto.getEmail() != null) {
            validateEmailUnique(dto.getEmail(), null);
        }

        Employee dbEmployee = employeeRepository.save(employeeMapper.toEntity(dto));
        return employeeMapper.toResponse(dbEmployee);
    }

    @Override
    public Page<EmployeeResponseDto> findAll(String firstName, String lastName, String email, Pageable pageable) {
        return employeeRepository.findAll(EmployeeSpecifications.filter(firstName, lastName, email), pageable)
                .map(employeeMapper::toResponse);
    }

    @Override
    public EmployeeResponseDto findById(Long id) {
        Employee dbEmployee = findEmployeeOrThrow(id);
        return employeeMapper.toResponse(dbEmployee);
    }

    @Transactional
    @Override
    public EmployeeResponseDto update(Long id, EmployeePutDto dto) {
        Employee dbEmployee = findEmployeeOrThrow(id);
        validateVersion(dto.getVersion(), dbEmployee.getVersion());
        if (dto.getEmail() != null) {
            validateEmailUnique(dto.getEmail(), id);
        }


        dbEmployee.setFirstName(dto.getFirstName());
        dbEmployee.setLastName(dto.getLastName());
        dbEmployee.setEmail(dto.getEmail());
        dbEmployee.setVersion(dto.getVersion());

        Employee updatedEmployee = employeeRepository.save(dbEmployee);
        return employeeMapper.toResponse(updatedEmployee);
    }

    @Transactional
    @Override
    public EmployeeResponseDto patchDto(Long id, EmployeePatchDto dto) {
        Employee dBemployee = findEmployeeOrThrow(id);
        validateVersion(dto.getVersion(), dBemployee.getVersion());

        if (dto.getFirstName() != null) {
            dBemployee.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null) {
            dBemployee.setLastName(dto.getLastName().trim());
        }
        if (dto.getEmail() != null) {
            validateEmailUnique(dto.getEmail(), id);
            dBemployee.setEmail(dto.getEmail().trim());
        }

        return employeeMapper.toResponse(employeeRepository.save(dBemployee));
    }


    @Override
    public void delete(Long id, Long version) {
        Employee dBemployee =findEmployeeOrThrow(id);
        validateVersion(version, dBemployee.getVersion());

        employeeRepository.delete(dBemployee);
    }

    private void validateEmailUnique(String email, Long currentEmployeeId) {
        String emailTrimmed = email.trim();
        employeeRepository.findByEmail(emailTrimmed)
                .filter(emp -> !emp.getId().equals(currentEmployeeId))
                .ifPresent(emp -> {
                    throw new EmailAlreadyInUseException(emailTrimmed);
                });
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private void validateVersion(Long expected, Long actual) {
        if (!Objects.equals(expected, actual)) {
            throw new VersionMismatchException(expected, actual);
        }
    }

}
