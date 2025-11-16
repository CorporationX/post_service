package faang.school.postservice.client;


import faang.school.postservice.dto.ai.CorrectionDto;
import faang.school.postservice.exception.AiServiceException;
import faang.school.postservice.exception.MissingAiConfigException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class AIClient {

    private final RestTemplate restTemplate;

    @Value("${ai.spellcheck-url}")
    private String spellcheckUrl;

    @PostConstruct
    public void validateConfig() {
        if (spellcheckUrl == null || spellcheckUrl.isBlank()) {
            throw new MissingAiConfigException(
                    "Required configuration property 'ai.spellcheck-url' is missing or empty"
            );
        }

        log.info("AIClient initialized with spellcheck URL: {}", spellcheckUrl);
    }


    public String correctText(String text) {
        try {
            String body = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            CorrectionDto[] correctionsArray = restTemplate.postForObject(
                    spellcheckUrl,
                    request,
                    CorrectionDto[].class
            );

            List<CorrectionDto> corrections = correctionsArray == null ? List.of() : Arrays.asList(correctionsArray);

            if (corrections.isEmpty()) {
                log.info("No spelling errors found");
                return text;
            }

            String corrected = applyCorrections(text, corrections);
            log.info("Corrected text: {}", corrected);
            return corrected;

        } catch (Exception e) {
            log.error("Error accessing AI Speller: {}", e.getMessage(), e);
            throw new AiServiceException("Error connecting to the spelling service", e);
        }
    }

    private HttpEntity<String> buildFormRequest(String text) {
        String body = "text=" + URLEncoder.encode(text, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<>(body, headers);
    }

    /**
     * Применяет список исправлений к исходному тексту.
     *
     * @param text        Исходный текст, который нужно исправить
     * @param corrections Список исправлений. Каждое исправление должно быть картой с ключами:
     *                    "pos" - позиция в тексте (int),
     *                    "len" - длина исправляемого фрагмента (int),
     *                    "s"   - список вариантов исправления (List<String>)
     * @return Исправленный текст
     */
    public static String applyCorrections(String text, List<CorrectionDto> corrections) {

        corrections.sort((a, b) -> b.pos() - a.pos());

        StringBuilder sb = new StringBuilder(text);

        for (CorrectionDto correction : corrections) {
            List<String> suggestions = correction.suggestions();
            if (suggestions == null || suggestions.isEmpty()) continue;

            int pos = correction.pos();
            int len = correction.len();

            sb.replace(pos, pos + len, suggestions.get(0));
        }

        return sb.toString();
    }

    private List<String> extractSuggestions(Map<String, Object> correction) {
        Object s = correction.get("s");

        if (s instanceof List<?> list) {
            return list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .toList();
        }

        return List.of();
    }
}
