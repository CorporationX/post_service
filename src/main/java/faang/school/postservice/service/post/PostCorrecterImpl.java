package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.JsonNode;
import faang.school.postservice.config.corrector.PostCorrectorProperty;
import faang.school.postservice.exception.TextAutoCorrectionException;
import faang.school.postservice.model.text.CorrectionResponse;
import faang.school.postservice.service.PostCorrectorService;
import faang.school.postservice.service.text.TextCorrectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class PostCorrecterImpl implements PostCorrectorService {
    private final PostCorrectorProperty properties;
    private final TextCorrectionService textCorrectionService;

    @Override
    @Retryable(retryFor = {IOException.class}, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 1.5))
    public String checkText(String textToCheck) {
        StringBuilder correctedText = new StringBuilder();
        String params = "text=" + URLEncoder.encode(textToCheck, StandardCharsets.UTF_8)
                + "&language=" + properties.language();
        CorrectionResponse correctionResponse = textCorrectionService.callCorrectionApi(params);
            int previousEnd = 0;
            assert correctionResponse != null;
            for (CorrectionResponse.Match match : correctionResponse.getMatches()) {
                if (match.getReplacements() != null && !match.getReplacements().isEmpty()) {
                    String replacement = match.getReplacements().get(0).getValue();
                    int offset = match.getOffset();
                    int length = match.getLength();
                    correctedText.append(textToCheck, previousEnd, offset);
                    correctedText.append(replacement);
                    previousEnd = offset + length;
                }
            }
            correctedText.append(textToCheck.substring(previousEnd));
        return correctedText.toString();
    }
}