package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Resource;

import java.util.List;

@Builder
public record CreatePostDto(
    Long authorId,
    Long projectId,
    @Size(max=4096, message = "The content of the post cannot exceed 4096 characters")
    @NotBlank(message = "The content of the post cannot be empty")
    String content,
    List<Album> albums,
    List<Resource> resources
){
}
