package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.comment.PostDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdDto {
    private Long id;

    @NotNull(message = "Post cannot be null")
    private PostDto post;

    @NotNull(message = "Buyer ID cannot be null")
    private Long buyerId;

    @NotNull(message = "Appearances left cannot be null")
    private Long appearancesLeft;

    @NotNull(message = "Start date cannot be null")
    private LocalDateTime startDate;

    @NotNull(message = "End date cannot be null")
    private LocalDateTime endDate;
}
