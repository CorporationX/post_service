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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void addLikePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException("Post with id %d not found".formatted(userId)));
        boolean likeExist = post
                .getLikes()
                .stream()
                .anyMatch(like -> like.getUserId().equals(userId));

        userServiceClient.getUser(userId);

        if (likeExist) {
            throw new IllegalArgumentException("Like already exists");
        }
        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .build();
        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void removeLikePost(Long postId, Long userId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post with id %d was not found".formatted(postId));
        }

        userServiceClient.getUser(userId);

        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }


    @Override
    @Transactional
    public void addLikeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException("Comment with id %d was not found".formatted(commentId)));
        boolean likeExist = comment
                .getLikes()
                .stream()
                .anyMatch(like -> like.getUserId().equals(userId));

        userServiceClient.getUser(userId);


        if (likeExist) {
            throw new IllegalArgumentException("Like already exists");
        }
        Like like = Like.builder()
                .comment(comment)
                .userId(userId)
                .build();
        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void removeLikeComment(Long commentId, Long userId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with id %d was not found".formatted(commentId));
        }

        userServiceClient.getUser(userId);

        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}
