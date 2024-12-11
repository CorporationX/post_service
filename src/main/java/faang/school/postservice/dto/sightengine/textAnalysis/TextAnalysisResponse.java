package faang.school.postservice.dto.sightengine.textAnalysis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextAnalysisResponse {
    @JsonProperty("status")
    private String status;
    @JsonProperty("request")
    private Request request;
    @JsonProperty("moderation_classes")
    private ModerationClasses moderationClasses;
}
