package com.pocketmon.insightpocket.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class IngestApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-KEY";

    @Value("${app.ingest.api-key:}")
    private String expectedApiKey;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) return true; // CORS preflight 허용
        if (!"POST".equalsIgnoreCase(method)) return true;

        return !(uri.startsWith("/api/") && uri.endsWith("/ingest"));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (!StringUtils.hasText(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write("{\"message\":\"INGEST_API_KEY is not configured\"}");
            return;
        }

        String provided = request.getHeader(HEADER);
        if (!StringUtils.hasText(provided) || !provided.equals(expectedApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write("{\"message\":\"Unauthorized\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}