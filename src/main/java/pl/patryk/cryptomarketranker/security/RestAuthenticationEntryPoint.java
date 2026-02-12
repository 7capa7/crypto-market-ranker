package pl.patryk.cryptomarketranker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.patryk.cryptomarketranker.utils.ErrorResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper om;

    public RestAuthenticationEntryPoint(ObjectMapper om) {
        this.om = om;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "Bearer");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        om.writeValue(response.getOutputStream(), ErrorResponse.unauthorized(request.getRequestURI(), "Invalid or missing authentication token"));
    }
}