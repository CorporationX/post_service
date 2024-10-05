package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;

    public void userValidation(Long userId) {
        log.info("Проверка существования пользователя с ID: {}", userId);
        if (userServiceClient.getUser(userId) == null) {
            throw new RuntimeException("Пользователь с ID " + userId + " не найден");
        }
    }

    public void likeValidation(LikeDto likeDto) {
        if (likeDto.getUserId() == null) {
            throw new IllegalArgumentException("ID пользователя не должен быть null");
        }
        if (likeDto.getPostId() == null && likeDto.getCommentId() == null) {
            throw new IllegalArgumentException("Должен быть указан либо ID поста, либо ID комментария");
        }
        if (likeDto.getPostId() != null && likeDto.getCommentId() != null) {
            throw new IllegalArgumentException("Можно указать либо ID поста, либо ID комментария, но не оба сразу");
        }
    }

    public void validatePostExists(Long postId) {
        log.info("Проверка существования поста с ID: {}", postId);
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Пост с ID " + postId + " не найден");
        }
    }

    public void validateCommentExists(Long commentId) {
        log.info("Проверка существования комментария с ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Комментарий с ID " + commentId + " не найден");
        }
    }
}