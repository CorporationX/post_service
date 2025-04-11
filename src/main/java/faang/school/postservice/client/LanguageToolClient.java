package faang.school.postservice.client;

import faang.school.postservice.dto.languageTool.LanguageToolResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageToolClient {

    @Value("${servers.language-tool.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public LanguageToolResponseDto getCorrectedText(String text, String language) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("text", text);
        formData.add("language", language);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);

        LanguageToolResponseDto response = restTemplate.exchange(baseUrl + "/check", HttpMethod.POST,
                httpEntity, LanguageToolResponseDto.class).getBody();

        return response;
    }
}
