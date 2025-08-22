package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;

public class CommentServiceImplData {

    public static Comment buildCommentEntity(Long authorId, Post post, String content) {
        Comment comment = new Comment();
        comment.setAuthorId(authorId);
        comment.setPost(post);
        comment.setContent(content);

        return comment;
    }

    public static Comment buildCommentEntity(Long commentId, Long authorId, Post post, String content,
                                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        Comment comment = buildCommentEntity(authorId, post, content);
        comment.setId(commentId);
        comment.setCreatedAt(createdAt);
        comment.setUpdatedAt(updatedAt);

        return comment;
    }

    public static CommentViewDto buildCommentViewDto(Comment entity) {
        Long id = entity.getId();
        String content = entity.getContent();
        Long authorId = entity.getAuthorId();
        String largeImageFileKey = entity.getLargeImageFileKey();
        String smallImageFileKey = entity.getSmallImageFileKey();
        LocalDateTime createdAt = entity.getCreatedAt();
        LocalDateTime updatedAt = entity.getUpdatedAt();
        Long likeCount = entity.getLikeCount();

        Long postId = null;

        return new CommentViewDto(
                id,
                content,
                authorId,
                postId,
                largeImageFileKey,
                smallImageFileKey,
                createdAt,
                updatedAt,
                likeCount
        );
    }
}