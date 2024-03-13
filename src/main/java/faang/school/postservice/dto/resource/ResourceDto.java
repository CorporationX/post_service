package faang.school.postservice.dto.resource;

import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceDto {
    private Long id;
    private long size;
    private String name;
    private String type;
    @NotNull
    private Long postId;
}
