package faang.school.postservice.model.dto.correcter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDetectionResponse {
    private String language;
    private String dialect;
    private Map<String, Double> probabilities;
}
