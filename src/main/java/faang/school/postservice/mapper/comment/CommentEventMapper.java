package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class CommentEventMapper {
    public CommentEventDto toEvent(Comment comment, Post post) {
        return CommentEventDto.builder()
                .commentId(comment.getId())
                .commentAuthorId(comment.getAuthorId())
                .postId(post.getId())
                .postAuthorId(post.getAuthorId())
                .commentContent(comment.getContent())
                .build();
    }
}
