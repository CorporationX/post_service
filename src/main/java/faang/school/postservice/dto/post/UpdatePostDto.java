package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePostDto(
    @NotNull(message="Content cant be null")
    @Size(max=4096, message="Can not exceed 4096 symbols")
    String content,
    boolean published
){
}
