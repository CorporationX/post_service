package faang.school.postservice.service.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceValidator {

    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    public void validateUserReal(LikeDto likeDto) {
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());
        if (!userDto.getId().equals(likeDto.getUserId())) {
            throw new DataValidationException("User by id:" + likeDto.getUserId() + " not found in ValidateUserReal");
        }
    }

    public void validateDuplicateLikeForPost(LikeDto likeDto) {
        List<Like> postLikes = likeRepository.findByPostId(likeDto.getPostId());
        for (Like like : postLikes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new DataValidationException("Post already liked!");
            }
        }
    }

    public void validateDuplicateLikeForComment(LikeDto likeDto) {
        List<Like> commentLikes = likeRepository.findByCommentId(likeDto.getCommentId());
        for (Like like : commentLikes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new DataValidationException("Comment already liked!");
            }
        }
    }

    public void validateLikeToPostAndCommentForComment(LikeDto likeDto) {
        if (getListPostLikes(likeDto).contains(likeDto.getUserId())) {
            throw new DataValidationException("Like already exist on the post!");
        }
    }

    public void validateLikeToPostAndCommentForPost(LikeDto likeDto) {
        if (getListCommentsLikesUnderPost(likeDto).contains(likeDto.getUserId())) {
            throw new DataValidationException("Like already exist on the comment!");
        }
    }

    private List<Long> getListPostLikes(LikeDto likeDto) {
        return  postRepository
                .findById(likeDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found in getListPostLikes"))
                .getLikes()
                .stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<Long> getListCommentsLikesUnderPost(LikeDto likeDto) {
        return postRepository
                .findById(likeDto.getPostId())
                .orElseThrow(() -> new DataValidationException("Post not found in getListCommentsLikesUnderPost"))
                .getComments()
                .stream()
                .flatMap(comment -> comment.getLikes().stream())
                .map(Like::getUserId)
                .toList();
    }

    public Post validateAndGetPost(LikeDto likeDto) {
        List<Post> postsUser = postRepository.findByAuthorIdWithLikes(likeDto.getUserId());
        Optional<Post> post = postsUser
                .stream()
                .filter(filter -> filter.getId() == likeDto.getPostId())
                .findFirst();
        if (post.isPresent()) {
            return post.get();
        } else {
            throw new DataValidationException("Post not found!");
        }
    }

    public Comment validateAndGetComment(LikeDto likeDto) {
        List<Comment> comments = commentRepository.findAllByPostId(likeDto.getPostId());
        Optional<Comment> comment = comments
                .stream()
                .filter(filter -> filter.getId() == likeDto.getCommentId())
                .findFirst();
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataValidationException("Comment not found!");
        }
    }
}
