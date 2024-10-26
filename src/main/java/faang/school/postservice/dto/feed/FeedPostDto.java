package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedPostDto {
    private PostDto postDto;
    private UserDto author;
    private List<CommentDto> comments;
    private List<UserDto> commentsAuthors;
}
