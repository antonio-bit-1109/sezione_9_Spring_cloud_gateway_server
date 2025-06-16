package com.example.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}


	//bean che serve per configurare una route alternativa da fornire nell url del gateway
// rispetto a quella generata di default dal gateway
	// in questo caso sto fornendo un url alternativo :
	// http://localhost:8072/pincopallo/accounts/*
	// invece del default http://localhost:8072/accounts/*
	@Bean
	public RouteLocator customRouteConfig(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes().route(p -> p.path("/pincopallo/accounts/**")
				.filters(filter ->
						filter.rewritePath(
								"/pincopallo/accounts/(?<segment>.*)" ,
								"/${segment}")
				)
				.uri("lb://ACCOUNTS"))
				.build();
	}

}
