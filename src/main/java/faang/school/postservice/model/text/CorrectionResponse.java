package faang.school.postservice.model.text;

import lombok.Data;

import java.util.List;

@Data
public class CorrectionResponse {
    private List<Match> matches;

    @Data
    public static class Match {
        private List<Replacement> replacements;
        private int offset;
        private int length;
    }

    @Data
    public static class Replacement {
        private String value;
    }
}
