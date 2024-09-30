package faang.school.postservice.service.spellcheck;

import faang.school.postservice.dto.correcter.LanguageDetectionResponse;
import faang.school.postservice.dto.correcter.SpellCheckResponse;
import faang.school.postservice.dto.correcter.TextGearsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpellCheckService {

    @Value("${spell-checker.api.host}")
    private String host;

    @Value("${spell-checker.api.key}")
    private String key;

    @Value("${spell-checker.api.autocorrect}")
    private String autocorrectPath;

    @Value("${spell-checker.api.language-detection}")
    private String languageDetectionPath;

    private final RestTemplate restTemplate;

    public String autoCorrect(String content, String language) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", content);

        String uri = UriComponentsBuilder.fromHttpUrl(host + autocorrectPath)
                .queryParam("key", key)
                .toUriString();

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TextGearsResponse<SpellCheckResponse>> APIresponse = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (APIresponse.getStatusCode().is2xxSuccessful()
                && APIresponse.getBody() != null
                && APIresponse.getBody().isStatus()) {
            return Objects.requireNonNull(APIresponse.getBody()).getResponse().getCorrected();
        } else {
            throw new RuntimeException("Spell checking failed: " + APIresponse.getStatusCode());
        }
    }

    public String detectLanguage(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("text", text);

        String uri = UriComponentsBuilder.fromHttpUrl(host + languageDetectionPath)
                .queryParam("key", key)
                .toUriString();

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<TextGearsResponse<LanguageDetectionResponse>> APIresponse = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );

        if (APIresponse.getStatusCode().is2xxSuccessful()
                && APIresponse.getBody() != null
                && APIresponse.getBody().isStatus()) {
            return Objects.requireNonNull(APIresponse.getBody()).getResponse().getLanguage();
        } else {
            throw new RuntimeException("Language detection failed: " + APIresponse.getStatusCode());
        }
    }
}
