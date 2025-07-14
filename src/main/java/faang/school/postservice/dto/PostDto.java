package faang.school.postservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private String content;
    private Long authorId;
    private List<Long> likeIds;
    private boolean published;
    private boolean deleted;
    private long numberOfLikes;
}
