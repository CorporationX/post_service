package faang.school.postservice.client;


import faang.school.postservice.dto.ai.AiRequestDto;
import faang.school.postservice.dto.ai.AiResponseDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
public class AIClient {

    private final RestTemplate restTemplate;
    private final String spellcheckUrl;

    public AIClient(RestTemplate restTemplate, @Value("${ai.spellcheck-url}") String spellcheckUrl) {
        this.restTemplate = restTemplate;
        this.spellcheckUrl = spellcheckUrl;
    }

    public String correctText(String text) {
        try {
            AiRequestDto request = new AiRequestDto(text);
            AiResponseDto response = restTemplate.postForObject(spellcheckUrl, request, AiResponseDto.class);

            if (response == null || response.correctedText() == null || response.correctedText().isBlank()) {
                throw new RuntimeException("Empty or invalid response from the AI service");
            }

            log.debug("The AI service successfully corrected the text with a length of {} characters.", text.length());
            return response.correctedText();

        } catch (HttpClientErrorException e) {
            log.warn("Client error while calling AI service ({}): {}", e.getStatusCode(), e.getMessage());
            throw new RuntimeException("AI service client error: " + e.getStatusCode(), e);

        } catch (HttpServerErrorException | ResourceNotFoundException e) {
            log.error("Server or network error while calling AI service: {}", e.getMessage());
            throw new RuntimeException("Error connecting to the AI service", e);

        } catch (Exception e) {
            log.error("An unexpected error occurred while accessing the AI service: {}", e.getMessage());
            throw new RuntimeException("Unable to edit text", e);
        }
    }
}
