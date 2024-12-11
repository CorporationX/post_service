package faang.school.postservice.service.moderation.sightengine;

import faang.school.postservice.config.resilience4j.Resilience4jProperties;
import faang.school.postservice.dto.sightengine.textAnalysis.TextAnalysisResponse;
import faang.school.postservice.exception.SightengineBadRequestException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SightEngineReactiveClient {
    private final WebClient webClient;

    @Value("${api.content-analysis-service.key}")
    private String apiKey;

    @Value("${api.content-analysis-service.secret}")
    private String apiSecret;

    @CircuitBreaker(name = Resilience4jProperties.DEFAULT_CIRCUIT_BREAKER_CONFIG_NAME)
    @Retry(name = Resilience4jProperties.DEFAULT_RETRY_CONFIG_NAME)
    public Mono<TextAnalysisResponse> analyzeText(String text) {
        MultiValueMap<String, String> requestBody = buildRequestBody(text);
        log.info("Creating a request for text analysis in the sightengine service: {}", requestBody);

        return webClient.post()
                .uri("/1.0/text/check.json")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                    response.bodyToMono(String.class)
                            .doOnError(e -> log.error("Bad Request error: {}",e.getMessage(), e))
                            .flatMap(body -> Mono.error(new SightengineBadRequestException(body))))
                .bodyToMono(TextAnalysisResponse.class);
    }

    private MultiValueMap<String, String> buildRequestBody(String text) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", text);
        body.add("lang", "ru,en");
        body.add("models", "general");
        body.add("mode", "ml");
        body.add("api-key", apiKey);
        body.add("api-secret", apiSecret);
        return body;
    }

    public boolean textAnalysisProcessing(TextAnalysisResponse response) {
        List<Double> analysisResults = response.getModerationClasses().collectingTextAnalysisResult();
        return analysisResults.stream()
                .allMatch(assessmentResult -> assessmentResult < 0.6);
    }
}
