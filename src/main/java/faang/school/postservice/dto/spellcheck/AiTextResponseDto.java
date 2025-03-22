package faang.school.postservice.dto.spellcheck;

import lombok.Data;

@Data
public class AiTextResponseDto {
    private InnerResponse response;

    @Data
    public static class InnerResponse {
        private String corrected;
    }

    public String getCorrected() {
        return response != null ? response.getCorrected() : null;
    }
}