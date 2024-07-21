package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@AllArgsConstructor
public class LikeValidator {
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;

    public void validate(LikeDto likeDto) {
        checkOnlyPostOrCommentNotNull(likeDto);
        if (likeDto.getPostId() != null) {
            checkPost(likeDto.getPostId());
        }
        if (likeDto.getCommentId() != null) {
            checkComment(likeDto.getCommentId());
        }
        checkUserExist(likeDto.getUserId());
    }

    private void checkUserExist(long userId) {
        if (userServiceClient.existById(userId) == null) {
            throw new NoSuchElementException(String.format("User with id = %d not exists", userId));
        }
    }

    private void checkPost(long postId) {
        if (!postService.existsById(postId)) {
            throw new NoSuchElementException(String.format("Post with id = %d not exists", postId));
        }
    }

    private void checkComment(long commentId) {
        if (!commentService.existsById(commentId)) {
            throw new NoSuchElementException(String.format("Comment with id = %d not exists", commentId));
        }
    }

    private void checkOnlyPostOrCommentNotNull(LikeDto likeDto) {
        if (likeDto.getCommentId() != null && likeDto.getPostId() != null) {
            throw new IllegalArgumentException("You can't like several things at the same time");
        }
        if (likeDto.getCommentId() == null && likeDto.getPostId() == null) {
            throw new IllegalArgumentException("You should like something at least");
        }
    }
}
