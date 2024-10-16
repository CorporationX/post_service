package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequestDto {
    @NotBlank(message = "Content can't be blank or empty")
    @Length(max = 4096, message = "Maximum number of characters 4096 symbols")
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private Long projectId;

    private LocalDateTime scheduledAt;
}
