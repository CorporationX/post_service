package faang.school.postservice.client;


import faang.school.postservice.dto.ai.AiRequestDto;
import faang.school.postservice.dto.ai.AiResponseDto;
import faang.school.postservice.exception.AiServiceException;
import faang.school.postservice.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final RestTemplate restTemplate;

    @Value("${ai.spellcheck-url:https://speller.yandex.net/services/spellservice.json/checkText}")
    private String spellcheckUrl;

    public String correctText(String text) {
        try {
            log.info("Отправляем текст на проверку орфографии ({} символов)", text.length());

            String body = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    spellcheckUrl,
                    HttpMethod.POST,
                    request,
                    (Class<List<Map<String, Object>>>) (Object) List.class);

            if (response.getBody() == null || response.getBody().isEmpty()) {
                log.info("Ошибок орфографии не найдено.");
                return text; // Ничего исправлять не нужно
            }

            String corrected = applyCorrections(text, response.getBody());
            log.info("Исправленный текст: {}", corrected);
            return corrected;

        } catch (Exception e) {
            log.error("Ошибка при обращении к Яндекс Спеллеру: {}", e.getMessage());
            throw new AiServiceException("Ошибка связи с сервисом орфографии", e);
        }
    }

    private String applyCorrections(String text, List<Map<String, Object>> corrections) {

        corrections.sort((a, b) -> ((Integer) b.get("pos")) - ((Integer) a.get("pos")));
        StringBuilder sb = new StringBuilder(text);

        for (Map<String, Object> correction : corrections) {
            List<String> suggestions = (List<String>) correction.get("s");
            if (suggestions == null || suggestions.isEmpty()) continue;

            int pos = (int) correction.get("pos");
            int len = (int) correction.get("len");

            sb.replace(pos, pos + len, suggestions.get(0));
        }

        return sb.toString();
    }
}
