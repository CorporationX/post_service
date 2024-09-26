package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    private final CommentRepository commentRepository;

    private final LikeMapper likeMapper;

    private final UserServiceClient userServiceClient;

    @SneakyThrows
    public LikeDto likePost(long postId, LikeDto likeDto) {
        log.info("User with ID {} is attempting to like post with ID {}", likeDto.userId(), postId);
        validateUser(likeDto.userId());
        if (likeRepository.findByPostIdAndUserId(postId, likeDto.userId()).isPresent()) {
            log.warn("Post with ID {} already has a like from user with ID {}", postId, likeDto.userId());
            throw new ValidationException("Post with id %d already has a like from user with id %d"
                    .formatted(postId, likeDto.userId()));
        }
        Like like = Like.builder()
                .userId(likeDto.userId())
                .post(postRepository.findById(postId).orElseThrow(() ->
                        new EntityNotFoundException("Post with id %d does not exist".formatted(postId))))
                .build();
        likeRepository.save(like);
        log.info("User with ID {} successfully liked post with ID {}", likeDto.userId(), postId);
        return likeMapper.toLikeDto(like);
    }

    public void deleteLikeFromPost(long postId, LikeDto likeDto) {
        log.info("User with ID {} is attempting to delete like from post with ID {}", likeDto.userId(), postId);
        likeRepository.deleteByPostIdAndUserId(postId, likeDto.userId());
        log.info("User with ID {} successfully deleted like from post with ID {}", likeDto.userId(), postId);
    }

    @SneakyThrows
    public LikeDto likeComment(long commentId, LikeDto likeDto) {
        log.info("User with ID {} is attempting to like comment with ID {}", likeDto.userId(), commentId);
        validateUser(likeDto.userId());
        if (likeRepository.findByCommentIdAndUserId(commentId, likeDto.userId()).isPresent()) {
            log.warn("Comment with ID {} already has a like from user with ID {}", commentId, likeDto.userId());
            throw new ValidationException("Comment with id %d already has a like from user with id %d"
                    .formatted(commentId, likeDto.userId()));
        }
        Like like = Like.builder()
                .userId(likeDto.userId())
                .comment(commentRepository.findById(commentId).orElseThrow(() ->
                        new EntityNotFoundException("Comment with id %d does not exist".formatted(commentId))))
                .build();
        likeRepository.save(like);
        log.info("User with ID {} successfully liked comment with ID {}", likeDto.userId(), commentId);
        return likeMapper.toLikeDto(like);
    }

    public void deleteLikeFromComment(long commentId, LikeDto likeDto) {
        log.info("User with ID {} is attempting to delete like from comment with ID {}", likeDto.userId(), commentId);
        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.userId());
        log.info("User with ID {} successfully deleted like from comment with ID {}", likeDto.userId(), commentId);
    }

    private void validateUser(long userId) {
        log.info("Validating user with ID {}", userId);
        userServiceClient.getUser(userId);
        log.info("User with ID {} validated successfully", userId);
    }
}
