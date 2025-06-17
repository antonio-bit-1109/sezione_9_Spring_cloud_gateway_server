package com.example.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseTraceFilter {

    private static final Logger logger = LoggerFactory.getLogger(ResponseTraceFilter.class);
    private final FilterUtility filterUtility;


    public ResponseTraceFilter(FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }


    /**
     * Defines a custom global filter to process HTTP requests and responses in a reactive Spring Cloud Gateway.
     * This method intercepts the response after the initial request chain is processed into the microservice,
     * then retrieves a correlation ID
     * from the request headers, and appends it to the response headers for tracking purposes.
     *
     * @return a custom {@link GlobalFilter} that updates the response headers with a correlation ID
     */
    @Bean
    public GlobalFilter postGlobalFilter() {
        return (exchange, chain) -> {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
                String correlationId = filterUtility.getCorrelationId(requestHeaders);
                logger.debug("Updated the correlation id to the outbound headers: {}", correlationId);
                exchange.getResponse().getHeaders().add(filterUtility.CORRELATION_ID, correlationId);
            }));
        };
    }
}
