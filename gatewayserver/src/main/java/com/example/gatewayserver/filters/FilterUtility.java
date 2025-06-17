package com.example.gatewayserver.filters;

import org.springframework.stereotype.Component;

import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@Component
public class FilterUtility {

    public static final String CORRELATION_ID = "eazybank-correlation-id";


    /**
     * Retrieves the correlation ID from the provided HTTP headers.
     *
     * @param requestHeaders the HTTP headers of the incoming request
     * @return the correlation ID if present in the headers, otherwise null
     */
    public String getCorrelationId(HttpHeaders requestHeaders) {
        if (requestHeaders.get(CORRELATION_ID) != null) {
            List<String> requestHeaderList = requestHeaders.get(CORRELATION_ID);
            return requestHeaderList.stream().findFirst().get();
        } else {
            return null;
        }
    }

    // inserisce nell header della request un id univoco, utile per il tracciamento.

    /**
     * Sets a specific header in the request of the given {@link ServerWebExchange}.
     * This method modifies the request using a builder pattern to include the specified
     * header name and value, and then returns an updated {@link ServerWebExchange}.
     *
     * @param exchange the current {@link ServerWebExchange} containing the request to update
     * @param name     the name of the header to be added or updated in the request
     * @param value    the value of the header to be set in the request
     * @return a new {@link ServerWebExchange} instance with the updated request containing the specified header
     */
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange, String name, String value) {
        return exchange.mutate().request(exchange.getRequest().mutate().header(name, value).build()).build();
    }


    /**
     * Sets the correlation ID in the request header of the given {@link ServerWebExchange}.
     * This method updates the request within the exchange to include the correlation ID
     * under a predefined header name and returns a new {@link ServerWebExchange} instance.
     *
     * @param exchange      the current {@link ServerWebExchange} containing the request to update
     * @param correlationId the correlation ID to be added to the request header
     * @return a new {@link ServerWebExchange} instance with the updated request containing the correlation ID header
     */
    public ServerWebExchange setCorrelationId(ServerWebExchange exchange, String correlationId) {
        return this.setRequestHeader(exchange, CORRELATION_ID, correlationId);
    }
}
