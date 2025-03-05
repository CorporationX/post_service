package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;

import java.util.Collections;
import java.util.List;

public class CommentTestData {
    public static Comment getComment(Long id, String text, Long userId, Long postId, List<Like> likes) {
        return Comment.builder()
                .id(id)
                .content(text)
                .authorId(userId)
                .post(Post.builder().id(postId).build())
                .likes(likes)
                .build();
    }

    public static CommentDto getCommentDto(Long id, String text, Long userId, Long postId, List<Long> likeIds) {
        return CommentDto.builder()
                .id(id)
                .content(text)
                .authorId(userId)
                .postId(postId)
                .likeIds(likeIds)
                .build();
    }

    public static ResponseCommentDto getResponseCommentDto(Long id, String text, Long userId, Long postId, List<Long> likes) {
        return ResponseCommentDto.builder()
                .id(id)
                .content(text)
                .authorId(userId)
                .postId(postId)
                .likeIds(Collections.emptyList())
                .build();
    }

    public static Post getPost(Long postId, Long userId) {
        return Post.builder()
                .id(postId)
                .authorId(userId)
                .content("Test Content")
                .build();
    }
}
