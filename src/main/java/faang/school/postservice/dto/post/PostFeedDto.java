package faang.school.postservice.dto.post;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostFeedDto implements Comparable<PostFeedDto>{

    private long id;
    private String content;
    private UserDto userDto;
    private Long projectId;
    private LocalDateTime publishedAt;


    @Override
    public int compareTo(PostFeedDto o) {
        return o.publishedAt.compareTo(this.publishedAt);
    }
}