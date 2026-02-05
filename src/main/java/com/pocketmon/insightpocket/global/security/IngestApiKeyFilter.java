package com.pocketmon.insightpocket.global.security;

import com.pocketmon.insightpocket.global.response.ApiResponse;
import com.pocketmon.insightpocket.global.response.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@RequiredArgsConstructor
public class IngestApiKeyFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-KEY";

    private final ObjectMapper objectMapper;

    @Value("${app.ingest.api-key:}")
    private String expectedApiKey;

    private void writeError(HttpServletResponse response, ErrorCode code, Object detail) throws IOException {
        response.setStatus(code.getHttpStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Object> body = ApiResponse.onFailure(code, detail);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if (!StringUtils.hasText(expectedApiKey)) {
            writeError(response, ErrorCode.INGEST_API_KEY_NOT_CONFIGURED, null);
            return;
        }

        String provided = request.getHeader(HEADER);
        if (!StringUtils.hasText(provided) || !safeEquals(provided, expectedApiKey)) {
            writeError(response, ErrorCode.UNAUTHORIZED, null);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean safeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(StandardCharsets.UTF_8), b.getBytes(StandardCharsets.UTF_8));
    }
}