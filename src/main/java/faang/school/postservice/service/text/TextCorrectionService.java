package faang.school.postservice.service.text;

import com.fasterxml.jackson.databind.JsonNode;
import faang.school.postservice.config.corrector.PostCorrectorProperty;
import faang.school.postservice.exception.TextAutoCorrectionException;
import faang.school.postservice.model.text.CorrectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

        ResponseEntity<CorrectionResponse> responseEntity =
                restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        new ParameterizedTypeReference<CorrectionResponse>() {});

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new TextAutoCorrectionException("Failed : HTTP error code : " + responseEntity.getStatusCode());
        }
    }
}