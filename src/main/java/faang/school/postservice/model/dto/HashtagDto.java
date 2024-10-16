package faang.school.postservice.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class HashtagDto {
    @Min(1)
    private long id;
    @Min(1)
    private Long postId;
    @Max(30)
    @Pattern(regexp = "^#\\S+$")
    private String content;
}