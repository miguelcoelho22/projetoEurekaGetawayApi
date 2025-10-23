package com.example.api_gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;

import java.net.InetSocketAddress;

@Configuration
public class GatewayHttpClientConfig {

    @Value("${gateway.httpclient.source-ip}")
    private String sourceIpAddress;

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .bindAddress(
                        ()-> new InetSocketAddress(sourceIpAddress, 0)
                );
    }
}
