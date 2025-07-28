package faang.school.postservice.service.like.impl;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.like.kafka.KafkaLikePublisher;
import faang.school.postservice.service.like.redis.RedisLikeCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final RedisLikeCache redisLikeCache;
    private final KafkaLikePublisher kafkaLikePublisher;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public void addLike(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new IllegalArgumentException("Лайк под этим постом уже оставлен пользователем с id: " + userId + " id поста: " + postId);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Пост с id " + postId + " не найден"));

        checkUserExists(userId);
        checkPostExists(postId);

        Like like = new Like();
        like.setPost(post);
        like.setUserId(userId);
        likeRepository.save(like);

        kafkaLikePublisher.publishLikeEvent(new LikeEvent("POST_LIKE", userId, postId, null, LocalDateTime.now()));
        redisLikeCache.incrementLikes("post_likes:", postId);
    }

    @Override
    @Transactional
    public void removeLike(Long postId, Long userId) {
        if (likeRepository.findByPostIdAndUserId(postId, userId).isEmpty()) {
            throw new IllegalArgumentException("Лайк под этим постом не найден у пользователя с id: " + userId + " id поста: " + postId);
        }

        checkUserExists(userId);
        checkPostExists(postId);

        likeRepository.deleteByPostIdAndUserId(postId, userId);

        kafkaLikePublisher.publishLikeEvent(new LikeEvent("POST_UNLIKE", userId, postId, null, LocalDateTime.now()));
        redisLikeCache.decrementLikes("post_likes:", postId);

    }


    @Override
    @Cacheable(value = "postLikes", key = "#postId")
    public int getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public void checkUserExists(Long userId) {
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException("Пользователь с id " + userId + " не найден");
        }
    }
    public void checkPostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Пост с id " + postId + " не найден");
        }
    }

}
