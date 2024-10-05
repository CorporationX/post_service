package faang.school.postservice.dto.text.gears;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextGearsResponse {
    private Boolean status;
    private Response response;

    @JsonProperty("error_code")
    private Integer errorCode;

    private String description;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String corrected;
    }
}