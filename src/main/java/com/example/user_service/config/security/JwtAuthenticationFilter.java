package com.example.user_service.config.security;

import com.example.user_service.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7).trim();

        try {
            String userId = jwtUtils.extractUserId(jwt);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    var userDetails = userDetailsService.loadUserById(userId);

                    if (jwtUtils.isValidAccessToken(jwt, userId)) {
                        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                } catch (Exception e) {
                    log.error("Could not set user authentication: {}", e.getMessage());
                    writeErrorResponse(response, ErrorCode.UNAUTHORIZED, request.getRequestURI());
                }
            }
        } catch (Exception e) {
            log.error("JWT token validation error: {}", e.getMessage());
            // Do not throw exception, just continue with the filter chain
            writeErrorResponse(response, ErrorCode.UNAUTHENTICATED, request.getRequestURI());
            return;
        }

        filterChain.doFilter(request, response);
    }
    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        response.setContentType("application/json");
        response.setStatus(errorCode.getStatusCode().value());

        Map<String, Object> body = new HashMap<>();
        body.put("message", errorCode.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(body));
    }
}
