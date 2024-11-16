package com.invensio.cavinator.config.security.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"x-requested-with, authorization, access-control-allow-headers, g-recaptcha-response, access-control-allow-origin, access-control-allow-methods, content-type");
		response.setHeader("Access-Control-Allow-Credentials", "false");
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(servletRequest, servletResponse);
		}

	}

	private String httpServletRequestToString(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();

		sb.append("Request Method = [" + request.getMethod() + "], ");
		sb.append("Request URL Path = [" + request.getRequestURL() + "], ");

		String headers = Collections.list(request.getHeaderNames()).stream()
				.map(headerName -> headerName + " : " + Collections.list(request.getHeaders(headerName)))
				.collect(Collectors.joining(", "));

		if (headers.isEmpty()) {
			sb.append("Request headers: NONE,");
		} else {
			sb.append("Request headers: [" + headers + "],");
		}

		String parameters = Collections.list(request.getParameterNames()).stream()
				.map(p -> p + " : " + Arrays.asList(request.getParameterValues(p))).collect(Collectors.joining(", "));

		if (parameters.isEmpty()) {
			sb.append("Request parameters: NONE.");
		} else {
			sb.append("Request parameters: [" + parameters + "].");
		}

		return sb.toString();
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
}
