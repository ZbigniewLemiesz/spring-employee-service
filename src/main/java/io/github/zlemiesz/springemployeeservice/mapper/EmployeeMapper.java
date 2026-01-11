package io.github.zlemiesz.springemployeeservice.dto;

import io.github.zlemiesz.springemployeeservice.model.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @author Zbigniew Lemiesz
 */
@Component
public class EmployeeMapper {
    private final ModelMapper mapper = new ModelMapper();
    
    public Employee toEntity(EmployeeDto dto) {
        return mapper.map(dto, Employee.class);
    }

    public EmployeeDto toDto(Employee entity) {
        return mapper.map(entity, EmployeeDto.class);
    }
}
