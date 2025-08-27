package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CommentCreateDto(
        @NotBlank(message = "Содержимое не может быть null")
        @Size(max = 4096, message = "Длинна содержимого не может быть больше 4096 символов")
        String content,

        @NotNull(message = "Имя автора не может быть null")
        Long authorId,

        @NotNull(message = "ID поста не может быть null")
        Long postId,

        String largeImageFileKey,
        String smallImageFileKey
) { }