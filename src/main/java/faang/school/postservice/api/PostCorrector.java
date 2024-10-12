package faang.school.postservice.api;

import faang.school.postservice.api.client.CorrectorClient;
import faang.school.postservice.dto.post.corrector.ApiResponse;
import faang.school.postservice.dto.post.corrector.AutoCorrectionResponse;
import faang.school.postservice.dto.post.corrector.CheckResponse;
import faang.school.postservice.dto.post.corrector.Error;
import faang.school.postservice.dto.post.corrector.LanguageResponse;
import faang.school.postservice.exception.CorrectorApiException;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "services.grammar-checker")
public class PostCorrector {
    private final CorrectorClient correctorClient;

    @Setter
    private Map<String, String> dialects;

    public void correctPost(Post post) {
        String languageDialect = getContentLanguageDialect(post.getContent());
        String correctedText;
        if (languageDialect.startsWith("en")) {
            correctedText = getAutoCorrectedEnglishText(post.getContent(), languageDialect);
        } else {
            correctedText = getCorrectedNonEnglishText(post.getContent(), languageDialect);
        }
        post.setContent(correctedText);
        log.debug("Post with id {} corrected", post.getId());
    }

    private String getContentLanguageDialect(String text) {
        ApiResponse<LanguageResponse> apiResponse = correctorClient.getContentLanguageResponse(text);
        if (apiResponse.status() == false || apiResponse.response().language() == null) {
            throwCorrectorApiException(apiResponse.errorCode(), apiResponse.description());
        }
        String language = apiResponse.response().language();
        return Optional.ofNullable(dialects.get(language)).orElseThrow(() -> {
            String exceptionMessage = "Dialect for this language: %s not found".formatted(language);
            log.error(exceptionMessage);
            return new CorrectorApiException("Dialect for this language: %s not found"
                    .formatted(language));
        });
    }

    private String getAutoCorrectedEnglishText(String text, String language) {
        ApiResponse<AutoCorrectionResponse> apiResponse =
                correctorClient.getAutoCorrectedEnglishTextResponse(text, language);
        if (apiResponse.status() == false) {
            throwCorrectorApiException(apiResponse.errorCode(), apiResponse.description());
        }
        return apiResponse.response().corrected();
    }

    private String getCorrectedNonEnglishText(String text, String language) {
        List<Error> errors = getNonEnglishCheckingResult(text, language);
        StringBuilder sb = new StringBuilder();
        int currIdx = 0;
        for (Error error : errors) {
            sb.append(text, currIdx, error.offset());
            sb.append(error.better().get(0));
            currIdx = currIdx + error.offset() + error.length();
        }
        if (currIdx <= text.length() - 1) {
            sb.append(text, currIdx, text.length());
        }
        return sb.toString();
    }

    private List<Error> getNonEnglishCheckingResult(String text, String language) {
        ApiResponse<CheckResponse> apiResponse = correctorClient.getCheckResponseForNonEnglishText(text, language);
        if (apiResponse.status() == false) {
            throwCorrectorApiException(apiResponse.errorCode(), apiResponse.description());
        }
        return apiResponse.response().errors();
    }

    private void throwCorrectorApiException(int errorCode, String description) {
        String exceptionMessage = "Error code: %d, %s"
                .formatted(errorCode, description);
        log.error(exceptionMessage);
        throw new CorrectorApiException(exceptionMessage);
    }
}
