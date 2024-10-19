package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.PostLikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.event.like.LikeKafkaEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.producer.kafka.KafkaLikeProducer;
import faang.school.postservice.publisher.PostLikePublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentService commentService;
    private final PostLikePublisher postLikePublisher;
    private final KafkaLikeProducer kafkaLikeProducer;

    @Transactional
    public LikeDto createLikeToPost(LikeDto likeDto) {
        if (likeDto.getLikeId() != null) {
            throw new DataValidationException("When creating a like, the likeId field must be empty!");
        }
        var post = postService.validationAndPostReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        var entityLike = likeMapper.toEntity(likeDto);
        entityLike.setPost(post);
        var createLike = likeRepository.save(entityLike);

        postLikePublisher.publish(PostLikeEventDto.builder()
                .postId(likeDto.getPostId())
                .actionUserId(likeDto.getLikeId())
                .authorId(post.getAuthorId())
                .build());
        LikeDto savedLikeDto = likeMapper.toDto(createLike);
        LikeKafkaEvent likeKafkaEvent = likeMapper.toEvent(savedLikeDto);
        likeKafkaEvent.setIncrement(true);
        kafkaLikeProducer.sendEvent(likeKafkaEvent);
        return savedLikeDto;
    }

    @Transactional
    public LikeDto removeLikeToPost(LikeDto likeDto) {
        if (likeDto.getLikeId() == null || !likeRepository.existsById(likeDto.getLikeId())) {
            throw new DataValidationException("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: " + likeDto.getLikeId());
        }
        var post = postService.validationAndPostReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        post.getLikes().remove(likeRepository.findById(likeDto.getLikeId()).get());
        likeRepository.delete(likeMapper.toEntity(likeDto));

        LikeKafkaEvent likeKafkaEvent = likeMapper.toEvent(likeDto);
        likeKafkaEvent.setIncrement(false);
        kafkaLikeProducer.sendEvent(likeKafkaEvent);

        return likeDto;
    }

    @Transactional
    public LikeDto createLikeToComment(LikeDto likeDto) {
        if (likeDto.getLikeId() != null) {
            throw new DataValidationException("When creating a like, the likeId field must be empty!");
        }
        var comment = commentService.validationAndCommentsReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        var entityLike = likeMapper.toEntity(likeDto);
        entityLike.setComment(comment);
        var createLike = likeRepository.save(entityLike);
        return likeMapper.toDto(createLike);
    }

    @Transactional
    public LikeDto removeLikeToComment(LikeDto likeDto) {
        if (likeDto.getLikeId() == null || !likeRepository.existsById(likeDto.getLikeId())) {
            throw new DataValidationException("It is not possible to delete a like with a null likeId or a non-existent like in the database likeId: " + likeDto.getLikeId());
        }
        var comment = commentService.validationAndCommentsReceived(likeDto);
        userServiceClient.getUser(likeDto.getUserId());
        comment.getLikes().remove(likeRepository.findById(likeDto.getLikeId()).get());
        likeRepository.delete(likeMapper.toEntity(likeDto));
        return likeDto;
    }
}
