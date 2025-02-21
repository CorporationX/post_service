package faang.school.postservice.dto.moderation;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ToxicityRequestDto {
    private String comment;
    private String[] languages;
    private Map<String, Object> requestedAttributes;
}
