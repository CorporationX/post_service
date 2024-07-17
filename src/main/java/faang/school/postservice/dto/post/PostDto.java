package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {

    @NotNull
    private long id;

    @NotBlank
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private Long projectId;

    @Future
    private LocalDateTime scheduledAt;

    //private long likesAmount;
}