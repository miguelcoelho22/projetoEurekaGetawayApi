package com.example.api_gateway.configuration;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

/**
 * Este filtro é responsável por:
 * 1. Obter um token de acesso do ConectaGov (Serpro).
 * 2. Implementar um cache reativo para o token (duração de 115 min).
 * 3. Adicionar o token "Bearer" à requisição antes de enviá-la ao serviço de destino (API de CEP).
 */
@Component
public class SerproAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<SerproAuthGatewayFilterFactory.Config> {

    // WebClient reativo
    private final WebClient.Builder webClientBuilder;

    // Este é o nosso "cache". É um Mono que guarda o resultado (o token)
    // por um tempo determinado.
    private final Mono<TokenResponse> cachedTokenMono;

    // Chave e Senha (do -u "<chave>:<senha>")
    // É MELHOR colocar isso no application.properties do que "hardcoded"
    private final String API_KEY = "CHAVE";
    private final String API_SECRET = "SENHA";



    // Classe de configuração (pode ficar vazia)
    public static class Config {}


    // Construtor
    public SerproAuthGatewayFilterFactory(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;

        // --- 1. DEFINIÇÃO DA LÓGICA DE BUSCAR O TOKEN ---
        // Traduzimos o comando cURL para WebClient
        Mono<TokenResponse> tokenRequestMono = this.webClientBuilder.build()
                .post()
                .uri("https://apigateway.conectagov.estaleiro.serpro.gov.br/oauth2/jwt-token")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")

                // O comando -u "<chave>:<senha>" é HTTP Basic Auth
                .headers(headers -> headers.setBasicAuth(API_KEY, API_SECRET))

                // O comando -d "grant_type=client_credentials"
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))

                .retrieve()
                .bodyToMono(TokenResponse.class);

        // --- 2. DEFINIÇÃO DA LÓGICA DE CACHE ---
        // O token dura 2h (120 min). Vamos fazer o cache durar 115 minutos
        // para ter uma margem de segurança.
        this.cachedTokenMono = tokenRequestMono
                .cache(Duration.ofMinutes(115));

        // O .cache() garante que, por 115 minutos, todas as requisições
        // que "assinarem" este Mono receberão a mesma resposta (o token)
        // sem disparar uma nova chamada HTTP.
    }


    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            // 1. Pede o token. O Mono.cache() vai decidir se busca um
            //    novo ou se usa o que está em memória.
            return this.cachedTokenMono.flatMap(tokenResponse -> {

                // 2. Extrai o token do objeto de resposta
                String token = tokenResponse.getAccess_token();

                // 3. Modifica a requisição ORIGINAL
                // (a que veio do usuário e já passou pelo Spring Security)
                var request = exchange.getRequest().mutate()
                        // Adiciona o token da API do Serpro
                        // O Serpro não diz qual header usar, mas o padrão é Authorization: Bearer
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .build();

                // 4. Continua a cadeia de filtros com a requisição MODIFICADA

                return chain.filter(exchange.mutate().request(request).build());
            });
        };
    }
}