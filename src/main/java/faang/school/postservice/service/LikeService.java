package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.like.LikeDto;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.messaging.likepost.LikeEventPublisher;
import faang.school.postservice.service.messaging.likepost.LikePostEvent;
import faang.school.postservice.util.ExceptionThrowingValidator;
import faang.school.postservice.validator.LikeValidator;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LikeService {

    private static final int BATCH_SIZE = 100;

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ExceptionThrowingValidator validator;
    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;

    private final LikeEventPublisher likeEventPublisher;

    public List<UserDto> getAllUsersLikedPost(long postId) {
        List<Like> likesByPostId = likeRepository.findByPostId(postId);
        List<Long> userIds = likesByPostId.stream().map(Like::getUserId).toList();
        List<UserDto> usersByButches = getUsersByButches(userIds);
        usersByButches.forEach(validator::validate);
        return usersByButches;
    }

    public List<UserDto> getAllUsersLikedComment(long commentId) {
        List<Like> likesByCommentId = likeRepository.findByCommentId(commentId);
        List<Long> userIds = likesByCommentId.stream().map(Like::getUserId).toList();
        List<UserDto> usersByButches = getUsersByButches(userIds);
        usersByButches.forEach(validator::validate);
        return usersByButches;
    }

    private List<UserDto> getUsersByButches(List<Long> userIds) {

        List<UserDto> users = new ArrayList<>();

        for (int indexFromInclusive = 0; indexFromInclusive < userIds.size(); indexFromInclusive += BATCH_SIZE) {

            int indexToExclusive = Math.min(indexFromInclusive + BATCH_SIZE, userIds.size());
            List<Long> batchIds = userIds.subList(indexFromInclusive, indexToExclusive);

            try {
                users.addAll(userServiceClient.getUsersByIds(batchIds));
            } catch (FeignException ex1) {
                log.info("Exception when requesting users from userServiceClient by batch, ids {}", userIds, ex1);
                users.addAll(getUserById(batchIds));
            }
        }

        return users;
    }

    private List<UserDto> getUserById(List<Long> batchIds) {

        List<UserDto> users = new ArrayList<>();

        for (Long userId : batchIds) {
            try {
                UserDto user = userServiceClient.getUser(userId);
                users.add(user);
            } catch (FeignException ex2) {
                log.info("Exception when requesting user by id from userServiceClient, id {}", userId, ex2);
            }
        }

        return users;
    }

    @Transactional
    public LikeDto addLikeToPost(Long postId, LikeDto likeDto) {
        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validatePostExists(postId);

        if (likeRepository.findByPostIdAndUserId(postId, likeDto.getUserId()).isPresent()) {
            log.warn("Лайк уже поставлен пользователем с ID: {} на пост с ID: {}", likeDto.getUserId(), postId);
            throw new RuntimeException("Лайк уже поставлен");
        }
        likeDto.setPostId(postId);
        Like like = likeMapper.toEntity(likeDto);
        like.setCreatedAt(LocalDateTime.now());
        like.setComment(null); // иначе TransientPropertyValueException
        likeRepository.save(like);
        Long postAuthorId = getPostById(postId).getAuthorId(); // иначе like.getPost().getAuthorId() == null
        likeEventPublisher.publish(new LikePostEvent(like.getUserId(), like.getPost().getId(), postAuthorId));
        return likeMapper.toDto(like);
    }

    public LikeDto removeLikeFromPost(Long postId, LikeDto likeDto) {
        log.info("Попытка удалить лайк на пост с ID: {}", postId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validatePostExists(postId);

        likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
        log.info("Лайк успешно удалён пользователем с ID: {} на пост с ID: {}", likeDto.getUserId(), postId);
        return likeMapper.toDto(null);
    }

    public LikeDto addLikeToComment(Long commentId, LikeDto likeDto) {
        log.info("Попытка поставить лайк на комментарий с ID: {}", commentId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validateCommentExists(commentId);

        if (likeRepository.findByCommentIdAndUserId(commentId, likeDto.getUserId()).isPresent()) {
            log.warn("Лайк уже поставлен пользователем с ID: {} на комментарий с ID: {}", likeDto.getUserId(), commentId);
            throw new RuntimeException("Лайк уже поставлен");
        }
        likeDto.setCommentId(commentId);
        Like like = likeMapper.toEntity(likeDto);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);
        log.info("Лайк успешно поставлен пользователем с ID: {} на комментарий с ID: {}", likeDto.getUserId(), commentId);
        return likeMapper.toDto(like);
    }

    public LikeDto removeLikeFromComment(Long commentId, LikeDto likeDto) {
        log.info("Попытка удалить лайк на комментарий с ID: {}", commentId);

        likeValidator.userValidation(likeDto.getUserId());
        likeValidator.validateCommentExists(commentId);

        likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
        log.info("Лайк успешно удалён пользователем с ID: {} на комментарий с ID: {}", likeDto.getUserId(), commentId);
        return likeMapper.toDto(null);
    }

    public List<Long> getLikesFromPost(Long postId) {
        log.info("Запрос на получение лайков для поста с ID: {}", postId);
        List<Long> userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
        log.info("Найдено {} лайков для поста с ID: {}", userIds.size(), postId);
        return userIds;
    }

    public List<Long> getLikesFromComment(Long commentId) {
        log.info("Запрос на получение лайков для комментария с ID: {}", commentId);
        List<Long> userIds = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
        log.info("Найдено {} лайков для комментария с ID: {}", userIds.size(), commentId);
        return userIds;
    }

    private Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found with id: " + id));
    }
}
