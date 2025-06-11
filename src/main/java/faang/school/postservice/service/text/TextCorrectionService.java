package faang.school.postservice.service.text;

import com.fasterxml.jackson.databind.JsonNode;
import faang.school.postservice.config.corrector.PostCorrectorProperty;
import faang.school.postservice.exception.TextAutoCorrectionException;
import faang.school.postservice.model.text.CorrectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class TextCorrectionService {
    private final PostCorrectorProperty properties;
    private final RestTemplate restTemplate;

    public CorrectionResponse callCorrectionApi(String params) {
        String url = properties.apiUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<CorrectionResponse> responseEntity =
                    restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                            new ParameterizedTypeReference<CorrectionResponse>() {});
            return responseEntity.getBody();
        } catch (RestClientResponseException e) {
            log.error("Failed : HTTP error while post text autocorrecting. Text & language = {}", params, e);
            throw new TextAutoCorrectionException("Failed : HTTP error code : " + ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString()));
        }
    }
}