package faang.school.postservice.dto.post;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class PostEventDto {
    private Long authorId;
    private Long postId;
}
