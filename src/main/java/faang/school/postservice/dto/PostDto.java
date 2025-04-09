package faang.school.postservice.dto;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotNull
    private String content;

    private Long authorId;
    private Long projectId;
}
