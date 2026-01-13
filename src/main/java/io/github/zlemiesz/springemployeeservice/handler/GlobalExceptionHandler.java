package io.github.zlemiesz.springemployeeservice.handler;

import io.github.zlemiesz.springemployeeservice.exception.EmailAlreadyInUseException;
import io.github.zlemiesz.springemployeeservice.exception.EmployeeNotFoundException;
import io.github.zlemiesz.springemployeeservice.exception.VersionMismatchException;
import io.github.zlemiesz.springemployeeservice.handler.error.ValidationError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final URI ABOUT_BLANK = URI.create("about:blank");

    // ---------------- 404 ----------------

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        ProblemDetail pd = baseProblem(
                HttpStatus.NOT_FOUND,
                "Not Found",
                safeMessage(ex),
                request.getRequestURI()
        );
        addError(pd, "resource", safeMessage(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEmployeeNotFound(EmployeeNotFoundException ex,
                                                                HttpServletRequest request) {
        ProblemDetail pd = baseProblem(
                HttpStatus.NOT_FOUND,
                "Employee Not Found",
                safeMessage(ex),
                request.getRequestURI()
        );
        addError(pd, "resource", safeMessage(ex));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(pd);
    }

    // ---------------- 409 ----------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request) {
        ProblemDetail pd = baseProblem(
                HttpStatus.CONFLICT,
                "Conflict",
                "Data integrity violation",
                request.getRequestURI()
        );
        addError(pd, "conflict", safeMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleOptimisticLock(OptimisticLockingFailureException ex,
                                                              HttpServletRequest request) {
        ProblemDetail pd = baseProblem(
                HttpStatus.CONFLICT,
                "Conflict",
                "Optimistic lock conflict",
                request.getRequestURI()
        );
        addError(pd, "version", "Optimistic lock conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ProblemDetail> handleObjectOptimisticLock(ObjectOptimisticLockingFailureException ex,
                                                                    HttpServletRequest request) {
        ProblemDetail pd = baseProblem(
                HttpStatus.CONFLICT,
                "Conflict",
                "Optimistic lock conflict",
                request.getRequestURI()
        );
        addError(pd, "conflict", "Optimistic lock conflict");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(VersionMismatchException.class)
    public ResponseEntity<ProblemDetail> handleVersionMismatch(VersionMismatchException ex,
                                                               HttpServletRequest request) {
        ProblemDetail pd = baseProblem(
                HttpStatus.CONFLICT,
                "Version conflict",
                safeMessage(ex),
                request.getRequestURI()
        );
        addError(pd, "version", safeMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ProblemDetail> handleEmailAlreadyInUse(EmailAlreadyInUseException ex,
                                                                 HttpServletRequest request) {

        ProblemDetail pd = baseProblem(
                HttpStatus.CONFLICT,
                "Email conflict",
                safeMessage(ex),
                request.getRequestURI()
        );
        addError(pd, "email", safeMessage(ex));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(pd);
    }

    // ---------------- 400: @Valid (body validation) ----------------

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "Request validation failed",
                pathOf(request)
        );

        // budujemy errors przez helper -> init properties + mutowalność
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(fe -> addError(
                        pd,
                        fe.getField(),
                        fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid value"
                ));

        return ResponseEntity.badRequest().body(pd);
    }

    // ---------------- 400: constraint violations (@Validated params) ----------------

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more constraints were violated",
                request.getRequestURI()
        );

        ex.getConstraintViolations().forEach(v -> addError(
                pd,
                v.getPropertyPath() != null ? v.getPropertyPath().toString() : "param",
                v.getMessage() != null ? v.getMessage() : "Invalid value"
        ));

        return ResponseEntity.badRequest().body(pd);
    }

    // ---------------- 400: missing query param ----------------

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String instance = pathOf(request);

        String paramName = ex.getParameterName() != null ? ex.getParameterName() : "param";
        String expectedType = ex.getParameterType() != null ? ex.getParameterType() : "unknown";

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more constraints were violated",
                instance
        );

        addError(pd, paramName, "Parameter is required (expected type: " + expectedType + ")");

        return ResponseEntity.badRequest().body(pd);
    }

    // ---------------- 400: path/query param type mismatch ----------------

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                          HttpServletRequest request) {

        String expected = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String name = ex.getName() != null ? ex.getName() : "param";
        Object value = ex.getValue();

        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                "One or more constraints were violated",
                request.getRequestURI()
        );

        addError(pd, name, "Invalid value: " + value + " (expected type: " + expected + ")");

        return ResponseEntity.badRequest().body(pd);
    }

    // ---------------- 400: JSON parse/mapping ----------------

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        String instance = pathOf(request);
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);

        // 1) Unknown field (strict whitelist)
        if (root instanceof UnrecognizedPropertyException upe) {
            String fieldPath = unknownFieldPath(upe);

            ProblemDetail pd = baseProblem(
                    HttpStatus.BAD_REQUEST,
                    "Unknown field in request",
                    "Field is not allowed",
                    instance
            );
            addError(pd, fieldPath, "Field is not allowed");
            return ResponseEntity.badRequest().body(pd);
        }

        // 2) Wrong type
        if (root instanceof InvalidFormatException ife) {
            String fieldPath = mappingPath(ife);
            String expected = (ife.getTargetType() != null) ? ife.getTargetType().getSimpleName() : "unknown";

            ProblemDetail pd = baseProblem(
                    HttpStatus.BAD_REQUEST,
                    "Wrong field type",
                    "JSON field has wrong type",
                    instance
            );
            addError(pd, fieldPath, "Expected type: " + expected);
            return ResponseEntity.badRequest().body(pd);
        }

        // 3) Malformed JSON
        if (root instanceof StreamReadException) {
            ProblemDetail pd = baseProblem(
                    HttpStatus.BAD_REQUEST,
                    "Malformed JSON",
                    "JSON is not valid",
                    instance
            );
            addError(pd, "body", "JSON is not valid");
            return ResponseEntity.badRequest().body(pd);
        }

        // 4) Missing body
        if (ex.getMessage() != null && ex.getMessage().contains("Required request body is missing")) {
            ProblemDetail pd = baseProblem(
                    HttpStatus.BAD_REQUEST,
                    "Request body is missing",
                    "Request body is required",
                    instance
            );
            addError(pd, "body", "Request body is required");
            return ResponseEntity.badRequest().body(pd);
        }

        // 5) Fallback
        ProblemDetail pd = baseProblem(
                HttpStatus.BAD_REQUEST,
                "Unreadable request body",
                safeMessage(root),
                instance
        );
        addError(pd, "body", safeMessage(root));
        return ResponseEntity.badRequest().body(pd);
    }

    // ---------------- Final fallback: ensure ProblemDetail always ----------------

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {

        if (body instanceof ProblemDetail) {
            return super.handleExceptionInternal(ex, body, headers, statusCode, request);
        }

        HttpStatus status = HttpStatus.resolve(statusCode.value());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        ProblemDetail pd = baseProblem(
                status,
                status.getReasonPhrase(),
                safeMessage(ex),
                pathOf(request)
        );

        addError(pd, "body", safeMessage(ex));

        return super.handleExceptionInternal(ex, pd, headers, statusCode, request);
    }

    // ---------------- Helpers ----------------

    private ProblemDetail baseProblem(HttpStatus status, String title, String detail, String instancePath) {
        ProblemDetail pd = ProblemDetail.forStatus(status);
        pd.setType(ABOUT_BLANK);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setInstance(URI.create(instancePath));
        return pd;
    }

    private String pathOf(WebRequest request) {
        if (request instanceof ServletWebRequest swr) {
            return swr.getRequest().getRequestURI();
        }
        return "";
    }

    private String safeMessage(Throwable ex) {
        if (ex == null) return "Unexpected error";
        return (ex.getMessage() != null && !ex.getMessage().isBlank())
                ? ex.getMessage()
                : ex.getClass().getSimpleName();
    }

    /**
     * Append one ValidationError to pd.properties["errors"].
     *
     * IMPORTANT (Spring Boot 4 / Spring 7):
     * - pd.getProperties() can be null until you call pd.setProperty(...)
     * - List.of(...) is immutable -> we always re-set a mutable ArrayList
     */
    @SuppressWarnings("unchecked")
    private void addError(ProblemDetail pd, String field, String message) {

        Object existing = null;

        // getProperties() can be null -> guard
        Map<String, Object> props = pd.getProperties();
        if (props != null) {
            existing = props.get("errors");
        }

        List<ValidationError> errors = new ArrayList<>();

        if (existing instanceof List<?> list) {
            for (Object o : list) {
                if (o instanceof ValidationError ve) {
                    errors.add(ve);
                } else if (o instanceof Map<?, ?> m) {
                    // fail-safe if any old Map-based errors slipped in
                    Object f = m.get("field");
                    Object msg = m.get("message");
                    errors.add(new ValidationError(
                            f != null ? f.toString() : "field",
                            msg != null ? msg.toString() : "Invalid value"
                    ));
                }
            }
        }

        errors.add(new ValidationError(field, message));

        // This initializes properties and stores a MUTABLE list
        pd.setProperty("errors", errors);
    }

    // --- Jackson helpers (unknown field, wrong type path) ---

    private String unknownFieldPath(UnrecognizedPropertyException ex) {
        String prefix = extractPath(ex.getPath());
        String field = ex.getPropertyName();
        return prefix.isBlank() ? field : prefix + "." + field;
    }

    /**
     * Try to resolve field name for InvalidFormatException:
     * 1) ex.getPath()
     * 2) ex.getPathReference()
     * 3) parse from exception message (best-effort)
     */
    private String mappingPath(InvalidFormatException ex) {
        // 1) from getPath()
        String path = extractPath(ex.getPath());
        if (!path.isBlank()) return path;

        // 2) from getPathReference() if available (reflection)
        String pathRef = invokeString(ex, "getPathReference"); // e.g. EmployeePutDto["version"]
        if (pathRef != null) {
            String last = lastBracketField(pathRef);
            if (last != null) return last;
        }

        // 3) best-effort parse from message (through reference chain: ...["field"])
        String msg = safeMessage(ex);
        String last = lastBracketField(msg);
        if (last != null) return last;

        return "body";
    }

    private String lastBracketField(String text) {
        // matches ["fieldName"] and returns the last occurrence
        Pattern p = Pattern.compile("\\[\"([^\"]+)\"\\]");
        Matcher m = p.matcher(text);
        String last = null;
        while (m.find()) last = m.group(1);
        return last;
    }

    private String extractPath(List<?> path) {
        if (path == null || path.isEmpty()) return "";

        String joined = path.stream()
                .map(this::refSegment)
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining("."));

        return joined.replace(".[", "[");
    }

    private String refSegment(Object ref) {
        if (ref == null) return null;

        String fieldName = invokeString(ref, "getFieldName");
        if (fieldName != null && !fieldName.isBlank()) return fieldName;

        Integer idx = invokeInt(ref, "getIndex");
        if (idx != null && idx >= 0) return "[" + idx + "]";

        return null;
    }

    private String invokeString(Object target, String method) {
        try {
            Method m = target.getClass().getMethod(method);
            Object val = m.invoke(target);
            return val != null ? val.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    private Integer invokeInt(Object target, String method) {
        try {
            Method m = target.getClass().getMethod(method);
            Object val = m.invoke(target);
            if (val instanceof Integer i) return i;
            if (val instanceof Number n) return n.intValue();
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
