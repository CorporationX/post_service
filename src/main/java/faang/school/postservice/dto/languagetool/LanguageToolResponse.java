package faang.school.postservice.dto.languagetool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LanguageToolResponse {
    private List<Match> matches;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Match {
        private String message;
        private int offset;
        private int length;
        private List<Replacement> replacements;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Replacement {
        private String value;
    }
}
