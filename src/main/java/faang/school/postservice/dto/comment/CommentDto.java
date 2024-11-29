package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank
    @Size(max = 4096, message = "The allowed maximum length is 4096 characters.")
    private String content;

    @NotNull
    private Long authorId;

    private UserDto author;

    @NotNull
    private Long postId;
}
