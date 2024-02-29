package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdatePostDto {
    private Long id;
    private String content;
    @JsonProperty
    private Long resourceId;
}
