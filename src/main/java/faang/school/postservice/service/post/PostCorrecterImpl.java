package faang.school.postservice.service.post;

import faang.school.postservice.client.TextCorrectionClient;
import faang.school.postservice.config.corrector.PostCorrectorProperty;
import faang.school.postservice.dto.post.TextCheckResponse;
import faang.school.postservice.model.text.CorrectionResponse;
import faang.school.postservice.service.PostCorrectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class PostCorrecterImpl implements PostCorrectorService {
    private final PostCorrectorProperty properties;
    private final TextCorrectionClient textCorrectionService;

    @Override
    public TextCheckResponse checkText(String textToCheck) {
        String apiRequestParams = "text=" + URLEncoder.encode(textToCheck, StandardCharsets.UTF_8)
                                  + "&language=" + properties.language();
        CorrectionResponse correctionResponse = textCorrectionService.callCorrectionApi(apiRequestParams);
        int previousEnd = 0;
        StringBuilder correctedText = new StringBuilder();
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
        return new TextCheckResponse(correctedText.toString());
    }
}