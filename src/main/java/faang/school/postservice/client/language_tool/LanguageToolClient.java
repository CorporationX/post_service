package faang.school.postservice.client.language_tool;

import faang.school.postservice.config.client.web.language_tool.LanguageToolConfigurationProperties;
import faang.school.postservice.dto.post.LanguageToolClientResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Component
@Slf4j
public class LanguageToolClient {
    private final WebClient webClient;

    public LanguageToolClient(WebClient.Builder builder,
                              LanguageToolConfigurationProperties props) {
        this.webClient = builder
                .baseUrl(props.getUrl() + "/" + props.getVersion())
                .build();
    }

    public LanguageToolClientResponseDto checkSpelling(String content, String language) {
        log.debug("Language tool client sent request check spelling {}, language {}", content, language);
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("text", content);
        formData.add("language", language);

        LanguageToolClientResponseDto response = webClient
                .post()
                .uri("/check")
                .contentType(APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(LanguageToolClientResponseDto.class)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(2)))
                .block();

        log.debug("Language tool client got response check spelling {}", response);

        return response;
    }
}
