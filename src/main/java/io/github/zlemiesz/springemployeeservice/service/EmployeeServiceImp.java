package io.github.zlemiesz.springemployeeservice.employee;

import io.github.zlemiesz.springemployeeservice.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;

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
    public EmployeeDto create(EmployeeDto dto) {
        Employee dbEmployee = employeeRepository.save(employeeMapper.toEntity(dto));
        return employeeMapper.toDto(dbEmployee);
    }

    @Override
    public List<EmployeeDto> findAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeeDto findById(Long id) {
        Optional<Employee> dbEmployee = employeeRepository.findById(id);
        return dbEmployee.map(employeeMapper::toDto).orElseThrow(
                () -> new EntityNotFoundException("No entry was found for id: " + id)
        );
    }

    @Transactional
    @Override
    public EmployeeDto update(Long id, EmployeePutDto dto) {
        Employee dBEmployee = employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("No entry was found for id: " + id)
        );

        if (!dto.getVersion().equals(dBEmployee.getVersion())) {
            throw new OptimisticLockingFailureException("Version mismatch");
        }


        dBEmployee.setFirstName(dto.getFirstName());
        dBEmployee.setLastName(dto.getLastName());
        dBEmployee.setEmail(dto.getEmail());
        dBEmployee.setVersion(dto.getVersion());

        Employee updatedEmployee = employeeRepository.save(dBEmployee);
        return employeeMapper.toDto(updatedEmployee);
    }

    @Transactional
    @Override
    public EmployeeDto patchDto(Long id, EmployeePatchDto dto) {

        Employee dBemployee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException("No entry was found for id: " + id)
                );

        if (!dto.getVersion().equals(dBemployee.getVersion())) {
            throw new OptimisticLockingFailureException("Version mismatch");
        }
        if (dto.getFirstName() != null) {
            dBemployee.setFirstName(dto.getFirstName().trim());
        }
        if (dto.getLastName() != null) {
            dBemployee.setLastName(dto.getLastName().trim());
        }
        if (dto.getEmail() != null) {
            String emailTrimmed = dto.getEmail().trim();

            if (employeeRepository.existsByEmail(emailTrimmed)) {
                throw new DataIntegrityViolationException("Email already in use: " + emailTrimmed);
            }
            dBemployee.setEmail(emailTrimmed);
        }


        return employeeMapper.toDto(employeeRepository.save(dBemployee));
    }


    @Override
    public void delete(Long id) {

    }


}
