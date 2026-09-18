package com.codemonks.gameservice.filter;

import com.codemonks.gameservice.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserContextFilter extends OncePerRequestFilter {
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String userIdHeader = request.getHeader(USER_ID_HEADER);
            if (userIdHeader != null && !userIdHeader.isBlank()) {
                Long userId = Long.parseLong(userIdHeader);
                UserContext.setUserId(userId);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }
}