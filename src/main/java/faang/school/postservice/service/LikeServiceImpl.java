package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;


    @Override
    public void addLikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException("Post was not found"));

        userServiceClient.getUser(userId);
        if (post
                .getLikes()
                .stream()
                .anyMatch(like -> like.getUserId().equals(userId))) {
            throw new IllegalArgumentException("Like already exists");
        }
        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .build();
        likeRepository.save(like);
    }

    @Override
    public void removeLikePost(Long postId, Long userId) {
        if(!postRepository.existsById(postId)){
            throw new EntityNotFoundException("Post was not found");
        }

        userServiceClient.getUser(userId);

        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }


    @Override
    public void addLikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException("Comment was not found"));

        userServiceClient.getUser(userId);
        if (comment.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))) {
            throw new IllegalArgumentException("Like already exists");
        }
        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();
        likeRepository.save(like);


    }

    @Override
    public void removeLikeComment(Long commentId, Long userId) {
        if(!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment was not found");
        }

        userServiceClient.getUser(userId);

        likeRepository.deleteByPostIdAndUserId(commentId, userId);


    }
}
