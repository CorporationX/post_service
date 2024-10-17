package faang.school.postservice.dto.comment;

import faang.school.postservice.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventDto extends EventDto {
    private Long commentAuthorId;
    private Long postId;
    private Long commentId;
    private String commentText;
}
