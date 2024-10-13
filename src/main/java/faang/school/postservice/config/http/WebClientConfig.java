package faang.school.postservice.config.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)  // Заголовки по умолчанию
                .filter(this::errorHandler);
    }

    private Mono<ClientResponse> errorHandler(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
                .flatMap(res -> {
                    if (res.statusCode().is4xxClientError()) {
                        return Mono.error(new RuntimeException("Client Error: can't fetch currency rates"));
                    } else if (res.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException("Server Error: can't fetch currency rates"));
                    } else {
                        return Mono.just(res);
                    }
                });
    }
}
