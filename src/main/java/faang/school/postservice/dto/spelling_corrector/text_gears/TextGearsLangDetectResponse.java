package faang.school.postservice.dto.spelling_corrector.text_gears;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TextGearsLangDetectResponse implements TextGearsBodyResponse {
    private boolean status;
    private Integer error_code;
    private String description;
    private Response response;

    @Data
    public static class Response {
        private String language;
        private String dialect;
    }
}

