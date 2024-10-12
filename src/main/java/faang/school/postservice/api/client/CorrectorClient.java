package faang.school.postservice.api.client;

import faang.school.postservice.dto.post.corrector.ApiResponse;
import faang.school.postservice.dto.post.corrector.AutoCorrectionResponse;
import faang.school.postservice.dto.post.corrector.CheckResponse;
import faang.school.postservice.dto.post.corrector.LanguageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CorrectorClient {

    @Autowired
    @Qualifier("correctorWebClient")
    private final WebClient webClient;

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttemptsExpression = "${post.grammar-checker.attempts}",
            backoff = @Backoff(delayExpression = "${post.grammar-checker}",
                    multiplierExpression = "${post.grammar-checker.multiplier}"))
    public ApiResponse<LanguageResponse> getContentLanguageResponse(String content) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("detect")
                        .queryParam("text", content)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<LanguageResponse>>() {})
                .block();
    }


    @Retryable(
            retryFor = {WebClientException.class},
            maxAttemptsExpression = "${post.grammar-checker.attempts}",
            backoff = @Backoff(delayExpression = "${post.grammar-checker}",
                    multiplierExpression = "${post.grammar-checker.multiplier}"))
    public ApiResponse<AutoCorrectionResponse> getAutoCorrectedEnglishTextResponse(String content, String language) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("correct")
                        .queryParam("text", content)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AutoCorrectionResponse>>() {})
                .block();
    }

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttemptsExpression = "${post.grammar-checker.attempts}",
            backoff = @Backoff(delayExpression = "${post.grammar-checker}",
                    multiplierExpression = "${post.grammar-checker.multiplier}"))
    public ApiResponse<CheckResponse> getCheckResponseForNonEnglishText(String content, String language) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("grammar")
                        .queryParam("text", content)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CheckResponse>>() {})
                .block();
    }
}
