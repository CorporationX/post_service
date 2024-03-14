package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeServiceValidator {
    private final PostService postService;
    private final CommentService commentService;
    private final UserServiceClient userServiceClient;

    public void validateLikeOnPost(LikeDto likeDto) {
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with this Id does not exist !");
        }

        Post post = postService.getPostById(likeDto.getPostId());
        List<Like> likes = post.getLikes();
        List<Comment> comments = post.getComments();
        for (Like like : likes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new DataValidationException("Like on post already exist !");
            }
        }
        comments
                .forEach(comment -> comment.getLikes()
                        .forEach(like -> {
                            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                                throw new DataValidationException("Cannot like post and comment together !");
                            }
                        }));
    }

    public void validateLikeOnComment(LikeDto likeDto) {
        try {
            userServiceClient.getUser(likeDto.getUserId());
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with this Id does not exist !");
        }

        Comment comment = commentService.getComment(likeDto.getCommentId());
        List<Like> likes = comment.getLikes();
        List<Like> likesOnPost = comment.getPost().getLikes();
        for (Like like : likes) {
            for (Like likeOnPost : likesOnPost) {
                if (Objects.equals(like.getUserId(), likeOnPost.getUserId())) {
                    throw new DataValidationException("Cannot like post and comment together !");
                }
            }
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new DataValidationException("Like on comment already exist !");
            }
        }
    }
}