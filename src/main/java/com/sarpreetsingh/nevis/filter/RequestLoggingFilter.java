package com.sarpreetsingh.nevis.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import java.util.Set;

@Component
public class RequestLoggingFilter extends AbstractRequestLoggingFilter {

    private final Set<String> excludedUrls = Set.of("/actuator", "/swagger-ui");
    private final HttpServletResponse response;

    public RequestLoggingFilter(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.debug(String.format("After request [Status=%s]", HttpStatus.valueOf(response.getStatus())));
    }

    @Override
    protected boolean isIncludeQueryString() {
        return true;
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return excludedUrls.stream()
                .noneMatch(request.getRequestURI()::startsWith);
    }
}
