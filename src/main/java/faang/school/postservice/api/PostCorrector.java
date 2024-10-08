package faang.school.postservice.api;

import faang.school.postservice.dto.post.corrector.ApiResponse;
import faang.school.postservice.dto.post.corrector.AutoCorrectionResponse;
import faang.school.postservice.dto.post.corrector.CheckResponse;
import faang.school.postservice.dto.post.corrector.Error;
import faang.school.postservice.dto.post.corrector.LanguageResponse;
import faang.school.postservice.exception.CorrectorApiException;
import lombok.RequiredArgsConstructor;
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
public class PostCorrector {

    @Autowired
    @Qualifier("getCorrectorClient")
    private final WebClient correctorClient;

    private final Map<String, String> dialects = Map.ofEntries(
            Map.entry("en", "en-US"),
            Map.entry("fr", "fr-FR"),
            Map.entry("de", "de-DE"),
            Map.entry("pt", "pt-PT"),
            Map.entry("it", "it-IT"),
            Map.entry("ru", "ru-RU"),
            Map.entry("ar", "ar-AR"),
            Map.entry("es", "es-ES"),
            Map.entry("ja", "ja-JP"),
            Map.entry("zh", "zh-CN"),
            Map.entry("el", "el-GR"));

    public String correctPost(String content) {
        String language = getContentLanguage(content);
        if (language.startsWith("en")) {
            AutoCorrectionResponse result = getAutoCorrectionResult(content, language);
            return result.corrected();
        }
        CheckResponse result = getCheckingNonEnglishTextResult(content, language);
        return getCorrectedText(content, result.errors());
    }

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    private String getContentLanguage(String content) {
        Mono<ApiResponse<LanguageResponse>> monoResponse = correctorClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("detect")
                        .queryParam("text", content)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<LanguageResponse>>() {
                });
        ApiResponse<LanguageResponse> response = monoResponse.block();
        if (response.status() && response.response().language() != null) {
            return Optional.ofNullable(dialects.get(response.response().language())).orElseThrow(
                    () -> new CorrectorApiException("Doesn't have dialect for this language"));
        } else {
            throw new CorrectorApiException("Can't automatically detect language");
        }
    }

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    private CheckResponse getCheckingNonEnglishTextResult(String content, String language) {
        Mono<ApiResponse<CheckResponse>> monoResponse = correctorClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("grammar")
                        .queryParam("text", content)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<CheckResponse>>() {
                });
        ApiResponse<CheckResponse> response = monoResponse.block();
        if (response.status()) {
            return response.response();
        } else {
            throw new CorrectorApiException("Can't check text for errors");
        }
    }

    @Retryable(
            retryFor = {WebClientException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    private AutoCorrectionResponse getAutoCorrectionResult(String content, String language) {
        Mono<ApiResponse<AutoCorrectionResponse>> monoResponse = correctorClient.get()
                .uri(uriBuilder -> uriBuilder
                        .pathSegment("correct")
                        .queryParam("text", content)
                        .queryParam("language", language)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<AutoCorrectionResponse>>() {
                });
        ApiResponse<AutoCorrectionResponse> response = monoResponse.block();
        if (response.status()) {
            return response.response();
        } else {
            throw new CorrectorApiException("Can't automatically correcting text");
        }
    }

    private String getCorrectedText(String content, List<Error> errors) {
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
}
