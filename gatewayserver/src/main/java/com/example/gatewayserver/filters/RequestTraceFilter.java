package com.example.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//NB: UN GATEWAY SERVER NON USA I CLASSICI FILTRI SERVLET MA, AVENDO IMPLEMENTATO LA DIPENDENZA DI
// REACTIVE GATEWAY (SPRING CLOUD ROUTING) SFRUTTA I FILTRI DI REACTIVE GATEWAY


@Order(1) // filtro eseguito per primo
@Component // componente globale dell applicazione - singleton
public class RequestTraceFilter implements GlobalFilter {
    // globalFilter: filtro attivato per qualsiasi richiesta che arriva sul gateway server.


    private static final Logger logger = LoggerFactory.getLogger(RequestTraceFilter.class);
    private final FilterUtility filterUtility;

    public RequestTraceFilter(FilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }


    // catena di filtri presenti all interno del WebFilterFactory del gateway server
    // il metodo non torna nulla (void) ma passa l exchange, ovvero la request/response al filtro successivo della caatena
    // Mono: rappresenta un singolo oggetto,
    // Flux: rappresenta una collection di oggetti
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders requestHeader = exchange.getRequest().getHeaders();

        // check if a correlation ID is present inside the request header reached the gatewayserver
        if (isCorrelationIdPresent(requestHeader)) {
            logger.debug("eazyBank-correlation-id found in RequestTraceFilter : {}",
                    filterUtility.getCorrelationId(requestHeader));
        } else {
            // if is not present create new one.
            String correlationId = generateCorrelationId();
            exchange = filterUtility.setCorrelationId(exchange, correlationId);
            logger.debug("eazyBank-correlation-id generated in RequestTraceFilter : {}", correlationId);

        }
        return chain.filter(exchange);
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }


    /**
     * Checks if the correlation ID is present in the given request headers.
     *
     * @param requestHeaders the HTTP headers of the incoming request
     * @return true if the correlation ID exists in the headers, false otherwise
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        if (this.filterUtility.getCorrelationId(requestHeaders) != null) {
            return true;
        } else {
            return false;
        }
    }
}
