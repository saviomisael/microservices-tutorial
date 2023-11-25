package com.programming.techie.apigatewayservice;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouteValidator {
    public static final List<String> openApiEndpoints = List.of("/auth/register", "/auth/validateToken", "/auth/token", "/eureka", "/api-docs");

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream().noneMatch(uri -> request.getURI().getPath().contains(uri));
}
