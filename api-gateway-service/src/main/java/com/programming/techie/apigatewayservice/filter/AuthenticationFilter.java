package com.programming.techie.apigatewayservice.filter;

import com.programming.techie.apigatewayservice.RouteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private RouteValidator routeValidator;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if(routeValidator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION))
                    throw new RuntimeException("missing authorization header");

                String token = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).getFirst();

                if(token != null && token.startsWith("Bearer ")) {
                    token = Arrays.asList(token.split(" ")).get(1);
                }

                try {
                    // REST call to identity-service

                } catch(Exception ex) {
                    // throws runtime exception
                }
            }

            return chain.filter(exchange);
        }));
    }

    public static class Config {}
}
