package faang.school.postservice.config;

import faang.school.postservice.exception.CommentAnalyzerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class CommentAnalyzerConfig {
    @Value("${services.comment-analyzer.endpoint}")
    String baseUrl;

    @Bean
    public WebClient commentAnalyzerClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter(errorHandlingFilter())
                .build();
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                return clientResponse
                        .bodyToMono(String.class)
                        .defaultIfEmpty("No error details")
                        .flatMap(errorBody -> Mono.error(
                                new CommentAnalyzerException(
                                        "Comment analyzer API error", clientResponse.statusCode()
                                )
                        ));
            }
            return Mono.just(clientResponse);
        });
    }
}
