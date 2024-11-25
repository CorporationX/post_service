package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedCommentDto {

    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
}
