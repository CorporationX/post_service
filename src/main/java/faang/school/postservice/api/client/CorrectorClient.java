package faang.school.postservice.api.client;

import faang.school.postservice.dto.post.corrector.ApiResponse;
import faang.school.postservice.dto.post.corrector.AutoCorrectionResponse;
import faang.school.postservice.dto.post.corrector.CheckResponse;
import faang.school.postservice.dto.post.corrector.Error;
import faang.school.postservice.dto.post.corrector.LanguageResponse;
import faang.school.postservice.exception.CorrectorApiException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "services.grammar-checker")
public class CorrectorClient {

    @Autowired
    @Qualifier("correctorWebClient")
    private final WebClient webClient;

    @Setter
    private Map<String, String> dialects;

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttemptsExpression = "${post.grammar-checker.attempts}",
            backoff = @Backoff(delayExpression = "${post.grammar-checker}",
                    multiplierExpression = "${post.grammar-checker.multiplier}"))
    public String getContentLanguageDialect(String content) {
        Mono<ApiResponse<LanguageResponse>> monoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("detect")
                        .queryParam("text", content)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<LanguageResponse>>() {
                });
        ApiResponse<LanguageResponse> apiResponse = monoResponse.block();
        if (apiResponse.status() && apiResponse.response().language() != null) {
            Optional<String> dialect = Optional.ofNullable(dialects.get(apiResponse.response().language()));
            return dialect.orElseThrow(() -> {
                var ex = new CorrectorApiException("Doesn't have dialect for this language: %s"
                        .formatted(apiResponse.response().language()));
                log.error(ex.getMessage());
                return ex;
            });
        } else {
            String exceptionMessage = "Error code: %d, %s"
                    .formatted(apiResponse.error_code(), apiResponse.description());
            log.error(exceptionMessage);
            throw new CorrectorApiException(exceptionMessage);
        }
    }

    public String getCorrectedNonEnglishText(String content, String language) {
        CheckResponse checkResponse = getCheckResponseForNonEnglishText(content, language);
        List<Error> errors = checkResponse.errors();
        StringBuilder sb = new StringBuilder();
        int currIdx = 0;
        for (Error error : errors) {
            sb.append(content, currIdx, error.offset());
            sb.append(error.better().get(0));
            currIdx = currIdx + error.offset() + error.length();
        }
        if (currIdx <= content.length() - 1) {
            sb.append(content, currIdx, content.length());
        }
        return sb.toString();
    }

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttemptsExpression = "${post.grammar-checker.attempts}",
            backoff = @Backoff(delayExpression = "${post.grammar-checker}",
                    multiplierExpression = "${post.grammar-checker.multiplier}"))
    public String getAutoCorrectedEnglishText(String content, String language) {
        Mono<ApiResponse<AutoCorrectionResponse>> monoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("correct")
                        .queryParam("text", content)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AutoCorrectionResponse>>() {
                });
        ApiResponse<AutoCorrectionResponse> apiResponse = monoResponse.block();
        if (apiResponse.status()) {
            return apiResponse.response().corrected();
        } else {
            String exceptionMessage = "Error code: %d, %s"
                    .formatted(apiResponse.error_code(), apiResponse.description());
            log.error(exceptionMessage);
            throw new CorrectorApiException(exceptionMessage);
        }
    }

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttemptsExpression = "${post.grammar-checker.attempts}",
            backoff = @Backoff(delayExpression = "${post.grammar-checker}",
                    multiplierExpression = "${post.grammar-checker.multiplier}"))
    private CheckResponse getCheckResponseForNonEnglishText(String content, String language) {
        Mono<ApiResponse<CheckResponse>> monoResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("grammar")
                        .queryParam("text", content)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CheckResponse>>() {
                });
        ApiResponse<CheckResponse> apiResponse = monoResponse.block();
        if (apiResponse.status()) {
            return apiResponse.response();
        } else {
            String exceptionMessage = "Error code: %d, %s"
                    .formatted(apiResponse.error_code(), apiResponse.description());
            log.error(exceptionMessage);
            throw new CorrectorApiException(exceptionMessage);
        }
    }
}
