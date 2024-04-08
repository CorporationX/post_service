package faang.school.postservice.dto;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.event.PostViewEventKafka;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.hash.PostHash;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedDto {
    private Long postId;
    private String content;
    private Long authorId;
    private Long projectId;
    private UserDto userDto;
    private LocalDateTime publishedAt;
    private LinkedHashSet<CommentEventKafka> comments;
    private LinkedHashSet<LikeEventKafka> likes;
    private LinkedHashSet<PostViewEventKafka> postViews;

    public FeedDto(PostHash post, UserDto userDto) {
        this.postId = post.getPostId();
        this.content = post.getContent();
        this.authorId = post.getAuthorId();
        this.projectId = post.getProjectId();
        this.userDto = userDto;
        this.publishedAt = post.getPublishedAt();
        this.comments = post.getComments();
        this.likes = post.getLikes();
        this.postViews = post.getPostViews();
    }
}
