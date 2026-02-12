package pl.patryk.cryptomarketranker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.patryk.cryptomarketranker.config.SecurityProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Component
public class BearerFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;
    private static final String BEARER_PREFIX = "Bearer ";
    private final AuthenticationEntryPoint entryPoint;

    public BearerFilter(SecurityProperties securityProperties, AuthenticationEntryPoint entryPoint) {
        this.securityProperties = securityProperties;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);


        if (!hasBearer(authHeader)) {
            entryPoint.commence(request, response, null);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length()).trim();

        if (token.isEmpty() || !constantTimeEquals(token, securityProperties.bearerToken())) {
            entryPoint.commence(request, response, null);
            return;
        }

        var auth = new PreAuthenticatedAuthenticationToken(
                "api-client",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private static boolean hasBearer(String authHeader) {
        return authHeader != null
                && !authHeader.isBlank()
                && authHeader.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length());
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }
}
