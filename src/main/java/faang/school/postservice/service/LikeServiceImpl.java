package faang.school.postservice.service;

import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Override
    public void putLikeToPost(long postId) {
        Post post = getPost(postId);
        userValidation();        
        duplicatePostLikeValidation(postId);        

        Like like = Like.builder()
            .userId(userContext.getUserId())
            .post(post)
            .build();

        likeRepository.save(like);
    }

    @Override
    public void putLikeToComment(long commentId) {
        Comment comment = getComment(commentId);
        userValidation();
        duplicateCommentLikeValidation(commentId);

        Like like = Like.builder()
            .userId(userContext.getUserId())
            .comment(comment)
            .build();
        likeRepository.save(like);
    }

    @Override
    public void deleteLike(long likeId) {
        likeRepository.findById(likeId).orElseThrow(
            () -> new EntityNotFoundException(String.format(
                "Like %d is not found.", likeId
            )));
        likeRepository.deleteById(likeId);
    }

    @Override
    public long countLikesFor(PostDto postDto) {
        return 0;
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new EntityNotFoundException(String.format(
                "Post %d is not found.", postId
            )));
    }

    private void userValidation() {
        if (userServiceClient.getUser(userContext.getUserId()) == null) {
            throw new EntityNotFoundException(String.format(
                "User %d is not found.", userContext.getUserId()
            ));
        }
    }

    private void duplicatePostLikeValidation(long postId) {
        if (likeRepository.findByPostIdAndUserId(postId, userContext.getUserId()).isPresent()) {
            throw new IllegalArgumentException(String.format(
                "User %d already liked post %d.", userContext.getUserId(), postId
            ));
        }
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
            () -> new EntityNotFoundException(String.format(
                "Comment %d is not found.", commentId
            )));
    }

    private void duplicateCommentLikeValidation(long commentId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userContext.getUserId()).isPresent()) {
            throw new IllegalArgumentException(String.format(
                "User %d already liked comment %d.", 
                userContext.getUserId(), commentId
            ));
        }
    }
}
