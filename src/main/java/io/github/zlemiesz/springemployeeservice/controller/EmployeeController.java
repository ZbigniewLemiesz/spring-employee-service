package io.github.zlemiesz.springemployeeservice.controller;

import io.github.zlemiesz.springemployeeservice.dto.*;
import io.github.zlemiesz.springemployeeservice.dto.common.PageResponse;
import io.github.zlemiesz.springemployeeservice.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAnyRole('VIEWER','MANAGER','HR','ADMIN')")
    @GetMapping
    public PageResponse<EmployeeResponseDto> getAll(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            Pageable pageable
    ) {
        Page<EmployeeResponseDto> page = service.findAll(firstName, lastName, email, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    @PreAuthorize("hasAnyRole('VIEWER','MANAGER','HR','ADMIN')")
    @GetMapping("/{id}")
    public EmployeeResponseDto getById(@PathVariable @Positive Long id) {
        return service.findById(id);
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto create(@Valid @RequestBody EmployeeCreateDto dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PutMapping("/{id}")
    public EmployeeResponseDto update(@PathVariable @Positive Long id, @Valid @RequestBody EmployeePutDto dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('MANAGER','HR','ADMIN')")
    @PatchMapping("/{id}")
    public EmployeeResponseDto patch(@PathVariable @Positive Long id, @Valid @RequestBody EmployeePatchDto employeePatchDto) {
        return service.patchDto(id, employeePatchDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id, @RequestParam @NotNull @Positive Long version) {
        service.delete(id, version);
    }
}

