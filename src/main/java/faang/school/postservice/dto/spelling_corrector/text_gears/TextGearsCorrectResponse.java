package faang.school.postservice.dto.spelling_corrector.text_gears;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class TextGearsCorrectResponse implements TextGearsBodyResponse {
    private boolean status;
    @JsonProperty("error_code")
    private Integer errorCode;
    private String description;
    private Response response;

    @Data
    public static class Response {
        private String corrected;
    }
}

