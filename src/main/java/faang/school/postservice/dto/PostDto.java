package faang.school.postservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.util.List;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
public record PostDto(
        Long id,

        @NotNull(message = "Content cannot be null")
        @NotBlank(message = "Content cannot be blank")
        @Size(max = 5_000, message = "Content cannot be longer than 5,000 characters")
        String content,

        Long authorId,
        Long projectId,
        boolean published,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt,
        int amountLikes,
        List<Long> commentIds,
        List<Long> albumIds
) {
}
