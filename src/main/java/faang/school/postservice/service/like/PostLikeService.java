package faang.school.postservice.service.like;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.kafka.producer.KafkaLikeEventProducer;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.publisher.RedisLikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.like.post.PostLikeValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeValidator validator;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final LikeMapper mapper;
    private final RedisLikeEventPublisher redisLikeEventPublisher;
    private final KafkaLikeEventProducer kafkaLikeEventProducer;

    public LikeDto likePost(LikeDto dto) {
        validator.verifyCanLikePost(dto);

        Like like = mapper.toModel(dto);
        like.setPost(getPostById(dto.getPostId()));
        like = likeRepository.save(like);

        submitEvent(like);

        return mapper.toDto(like);
    }

    public void deletePostLike(@Valid LikeDto dto) {
        Long userId = dto.getUserId();
        validator.verifyLikeExists(userId);

        likeRepository.deleteByPostIdAndUserId(dto.getPostId(), userId);
    }

    public LikeDto getLikeById(Long likeId) {
        Like like = likeRepository.findById(likeId).orElseThrow(
                () -> new NotFoundException(String.format("Like with id %d not found", likeId))
        );

        return mapper.toDto(like);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new NotFoundException(String.format("Post with id %d not found", postId))
        );
    }

    private void submitEvent(Like like) {
        redisLikeEventPublisher.publish(mapper.toEventDto(like));
        kafkaLikeEventProducer.sendLikeEvent(like);
    }
}
