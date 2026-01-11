
package io.github.zlemiesz.springemployeeservice.employee;

import io.github.zlemiesz.springemployeeservice.handler.GlobalExceptionHandler;
import io.github.zlemiesz.springemployeeservice.model.Employee;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MVC slice tests for consistent RFC 9457 ProblemDetail responses.
 *
 * Spring Boot 4: @WebMvcTest lives in org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
 * and slice tests typically use @MockitoBean for collaborators. [1](https://javadoc.io/static/com.fasterxml.jackson.core/jackson-databind/2.19.2/com/fasterxml/jackson/databind/exc/UnrecognizedPropertyException.html)[2](https://stackoverflow.com/questions/56360374/how-to-fix-jackson-databind-version-in-spring-boot-starter-json-pom-xml)
 */
@WebMvcTest(controllers = EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeErrorHandlingMvcTest {

    private static final MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @Autowired
    MockMvc mockMvc;

    /**
     * If your controller depends on a service/facade, provide a Mockito stub.
     * Replace EmployeeService with the actual dependency type used by EmployeeController.
     */
    @MockitoBean
    EmployeeService employeeService;

    // -------------------- Helpers (common assertions) --------------------

    private void expectProblemBasics(String expectedInstance,
                                     int expectedStatus,
                                     String expectedTitle,
                                     org.springframework.test.web.servlet.ResultActions ra) throws Exception {

        ra.andExpect(status().is(expectedStatus))
                .andExpect(content().contentTypeCompatibleWith(PROBLEM_JSON))
                .andExpect(jsonPath("$.type").value("about:blank"))
                .andExpect(jsonPath("$.status").value(expectedStatus))
                .andExpect(jsonPath("$.title").value(expectedTitle))
                .andExpect(jsonPath("$.instance").value(expectedInstance))
                .andExpect(jsonPath("$.detail", not(isEmptyOrNullString())));
    }

    private void expectErrorsPresent(org.springframework.test.web.servlet.ResultActions ra) throws Exception {
        ra.andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", not(empty())))
                .andExpect(jsonPath("$.errors[0].field", not(isEmptyOrNullString())))
                .andExpect(jsonPath("$.errors[0].message", not(isEmptyOrNullString())));
    }

    // =====================================================================
    // 400 tests (JSON parse/mapping + @Valid validation)
    // =====================================================================

    /** 400: Unknown field (phone) */
    @Test
    void post_unknownField_shouldReturn400ProblemDetailWithErrors() throws Exception {
        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "email": "jan@x.pl",
                  "phone": 123
                }
                """;

        var ra = mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee", 400, "Unknown field in request", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors[0].field").value("phone"))
                .andExpect(jsonPath("$.errors[0].message").value("Field is not allowed"));
    }

    /** 400: Wrong type (version string instead of Long) */
    @Test
    void put_wrongType_shouldReturn400ProblemDetailAndFieldVersion() throws Exception {
        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "email": "jan@x.pl",
                  "version": "123OOO"
                }
                """;

        var ra = mockMvc.perform(put("/employee/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee/1", 400, "Wrong field type", ra);
        expectErrorsPresent(ra);

        // po poprawkach w handlerze oczekujemy "version" zamiast "body"
        ra.andExpect(jsonPath("$.errors[0].field").value("version"))
                .andExpect(jsonPath("$.errors[0].message", containsString("Expected type")));
    }

    /** 400: Malformed JSON */
    @Test
    void post_malformedJson_shouldReturn400ProblemDetail() throws Exception {
        // brak przecinka przed "email"
        String body = """
                {"firstName":"Jan","lastName":"Kowalski" "email":"jan@x.pl"}
                """;

        var ra = mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee", 400, "Malformed JSON", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors[0].field").value("body"))
                .andExpect(jsonPath("$.errors[0].message").value("JSON is not valid"));
    }

    /** 400: Missing body */
    @Test
    void post_missingBody_shouldReturn400ProblemDetail() throws Exception {
        var ra = mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON));

        expectProblemBasics("/employee", 400, "Request body is missing", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors[0].field").value("body"))
                .andExpect(jsonPath("$.errors[0].message").value("Request body is required"));
    }

    /** 400: @Valid validation errors (multiple errors in errors[]) */
    @Test
    void put_validationErrors_shouldReturn400ProblemDetailWithMultipleErrors() throws Exception {
        // przykład: version null + firstName blank
        String body = """
                {
                  "firstName": "",
                  "lastName": "Kowalski",
                  "email": "jan@x.pl",
                  "version": null
                }
                """;

        var ra = mockMvc.perform(put("/employee/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee/1", 400, "Validation failed", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors.length()", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.errors[*].field", hasItems("firstName", "version")));
    }

    // =====================================================================
    // 404 / 409 tests (require mocked service to throw)
    // =====================================================================

    @Test
    void put_whenEntityNotFound_shouldReturn404ProblemDetailWithErrors_resource() throws Exception {
        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "email": "jan@x.pl",
                  "version": 1
                }
                """;

        when(employeeService.update(anyLong(), any(EmployeePutDto.class)))
                .thenThrow(new EntityNotFoundException("Employee 999 not found"));

        var ra = mockMvc.perform(put("/employee/999")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee/999", 404, "Not Found", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors[0].field").value("resource"))
                .andExpect(jsonPath("$.errors[0].message", containsString("not found")));
    }

    @Test
    void post_whenDataIntegrityViolation_shouldReturn409ProblemDetailWithErrors_conflict() throws Exception {
        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "email": "jan@x.pl"
                }
                """;

        when(employeeService.create(any(EmployeeDto.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        var ra = mockMvc.perform(post("/employee")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee", 409, "Conflict", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors[0].field").value("conflict"));
    }

    @Test
    void put_whenOptimisticLockFailure_shouldReturn409ProblemDetailWithErrors_conflict() throws Exception {
        String body = """
                {
                  "firstName": "Jan",
                  "lastName": "Kowalski",
                  "email": "jan@x.pl",
                  "version": 1
                }
                """;

        when(employeeService.update(anyLong(), any(EmployeePutDto.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Employee.class, 1L));

        var ra = mockMvc.perform(put("/employee/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(PROBLEM_JSON)
                .content(body));

        expectProblemBasics("/employee/1", 409, "Conflict", ra);
        expectErrorsPresent(ra);

        ra.andExpect(jsonPath("$.errors[0].field").value("conflict"));
    }
}
