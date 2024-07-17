package faang.school.postservice.dto.post;

import faang.school.postservice.model.Hashtag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private Long authorId;
    private Long projectId;

    @NotBlank
    @Size(min = 1, max = 4000)
    private String content;

    @NotNull
    private List<Hashtag> hashtags;
}
