package faang.school.postservice.dto.sightengine.textAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ModerationClasses {
    @JsonProperty("available")
    private List<String> available;

    @JsonProperty("sexual")
    private Double sexual;

    @JsonProperty("discriminatory")
    private Double discriminatory;

    @JsonProperty("insulting")
    private Double insulting;

    @JsonProperty("violent")
    private Double violent;

    @JsonProperty("toxic")
    private Double toxic;

    public List<Double> collectingTextAnalysisResult() {
        List<Double> results = new ArrayList<>();
        results.add(sexual);
        results.add(discriminatory);
        results.add(insulting);
        results.add(violent);
        results.add(toxic);
        return results;
    }
}
