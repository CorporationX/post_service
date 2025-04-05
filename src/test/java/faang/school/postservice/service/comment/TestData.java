package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;

import java.time.LocalDateTime;

public class TestData {

    public static UserResponseDto createUserDto(Long id, String username, String email) {
        return UserResponseDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    public static Post createPost(Long postId, Long authorId) {
        return Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();
    }

    public static Like createLike(Long id, Long userId, Post post, Comment comment) {
        return Like.builder()
                .id(id)
                .userId(userId)
                .post(post)
                .comment(comment)
                .build();
    }

    public static Comment createComment(Long commentId, String content, Long authorId, Post post) {
        return Comment.builder()
                .id(commentId)
                .content(content)
                .authorId(authorId)
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static CommentRequestDto createCommentRequestDto(String content, Long authorId, Long postId) {
        return CommentRequestDto.builder()
                .content(content)
                .authorId(authorId)
                .postId(postId)
                .build();
    }

    public static Comment createCommentForBan(Long id, String content, boolean verified,Long authorId) {
        return Comment.builder()
                .id(id)
                .content(content)
                .verified(verified)
                .authorId(authorId)
                .build();
    }
}
