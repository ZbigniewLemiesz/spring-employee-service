package io.github.zlemiesz.springemployeeservice.controller;

import io.github.zlemiesz.springemployeeservice.dto.*;
import io.github.zlemiesz.springemployeeservice.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Zbigniew Lemiesz
 */

@RestController
@RequestMapping("/employee")
@Validated
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public Page<EmployeeResponseDto> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            Pageable pageable
    ) {
        return service.findAll(firstName, lastName, email, pageable);
    }

    @GetMapping("/{id}")
    public EmployeeResponseDto getById(@PathVariable @Positive Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto create(@Valid @RequestBody EmployeeCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public EmployeeResponseDto update(@PathVariable @Positive Long id, @Valid @RequestBody EmployeePutDto dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public EmployeeResponseDto patch(@PathVariable @Positive Long id, @Valid  @RequestBody EmployeePatchDto employeePatchDto){
        return service.patchDto(id, employeePatchDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id, @RequestParam @NotNull @Positive Long version) {
        service.delete(id, version);
    }
}

