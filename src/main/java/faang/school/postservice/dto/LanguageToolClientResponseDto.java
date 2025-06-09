package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageToolClientResponseDto {
    private List<Match> matches;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Match {
        private String message;
        private List<Replacement> replacements;
        private int offset;
        private int length;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Replacement {
        private String value;
    }
}
