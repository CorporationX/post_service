package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String content;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long authorId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long postId;

}
