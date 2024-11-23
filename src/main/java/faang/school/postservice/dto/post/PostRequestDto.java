package faang.school.postservice.dto.post;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {

    @Positive
    private Long authorId;

    @Positive
    private Long projectId;

    @NotBlank(message = "Post content is required")
    @Size(max = 4096, message = "Post content can not be longer 4096 characters")
    private String content;
}
