package io.github.zlemiesz.springemployeeservice.mapper;

import io.github.zlemiesz.springemployeeservice.dto.EmployeeCreateDto;
import io.github.zlemiesz.springemployeeservice.dto.EmployeeResponseDto;
import io.github.zlemiesz.springemployeeservice.model.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * @author Zbigniew Lemiesz
 */
@Component
public class EmployeeMapper {
    private final ModelMapper mapper;

    public EmployeeMapper(ModelMapper mapper){
        this.mapper = mapper;
    }

    public EmployeeResponseDto toResponse(Employee entity) {
        return mapper.map(entity, EmployeeResponseDto.class);
    }

    public Employee toEntity(EmployeeCreateDto dto) {
        return mapper.map(dto, Employee.class);
    }

}
