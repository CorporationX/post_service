package faang.school.postservice.dto;

import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private Long postId;

    private Long authorId;

    private String content;

}
