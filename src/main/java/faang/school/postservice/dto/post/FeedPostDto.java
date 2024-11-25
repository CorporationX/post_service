package faang.school.postservice.dto.post;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedPostDto {

    private Long id;
    private String content;
    private String authorName;
    private Integer likes;
}
