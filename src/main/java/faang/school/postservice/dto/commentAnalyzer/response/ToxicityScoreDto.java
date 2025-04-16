package faang.school.postservice.dto.commentAnalyzer.response;

import faang.school.postservice.enums.CommentToxicityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToxicityScoreDto {
    private Map<CommentToxicityType, AttributeScoreDto> attributeScores;
    private List<String> languages;
    private List<String> detectedLanguages;
}
