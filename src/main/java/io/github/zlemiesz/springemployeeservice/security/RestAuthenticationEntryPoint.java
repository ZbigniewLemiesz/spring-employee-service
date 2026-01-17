package io.github.zlemiesz.springemployeeservice.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author Zbigniew Lemiesz
 */

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final URI ABOUT_BLANK = URI.create("about:blank");
    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setType(ABOUT_BLANK);
        pd.setTitle("Unauthorized");
        pd.setDetail("Authentication is required");
        pd.setInstance(URI.create(request.getRequestURI()));
        pd.setProperty("errors", List.of(
                Map.of("field", "auth", "message", "Missing or invalid credentials/session")
        ));

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), pd);
    }
}

