package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.kafka.LikeCreatedEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.producer.KafkaProducer;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.redisPublisher.LikePostPublisher;
import faang.school.postservice.redisPublisher.PostLikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeServiceValidator;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeEventPublisher likeEventPublisher;
    @Value("${batch-size}")
    @Setter
    private int BATCH_SIZE;

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final LikeServiceValidator likeServiceValidator;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeMapper likeMapper;
    private final PostLikeEventPublisher postLikeEventPublisher;
    private final LikePostPublisher likePostPublisher;
    private final LikeEventMapper likeEventMapper;
    private final KafkaProducer kafkaProducer;

    public List<UserDto> getLikesUsersByPostId(Long postId) {

        List<Long> userIdsLikedPost = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsersDto(userIdsLikedPost);
    }

    public List<UserDto> getLikesUsersByCommentId(Long commentId) {

        List<Long> userIdsLikedComment = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsersDto(userIdsLikedComment);
    }

    private List<UserDto> getUsersDto(List<Long> userIdsLiked) {

        List<List<Long>> batchesList = new ArrayList<>();

        for (int i = 0; i < userIdsLiked.size(); i += BATCH_SIZE) {
            List<Long> batch = userIdsLiked.subList(i, Math.min(i + BATCH_SIZE, userIdsLiked.size()));
            batchesList.add(batch);
        }
        return collectUsersDto(batchesList);
    }

    private List<UserDto> collectUsersDto(List<List<Long>> batchesList) {

        List<UserDto> result = new ArrayList<>();

        for (List<Long> batch : batchesList) {
            result.addAll(userServiceClient.getUsersByIds(batch));
        }
        return result;
    }

    @Transactional
    public LikeDto addLikeToPost(LikeDto likeDto) {
        Post post = postService.getPost(likeDto.getPostId());
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());

        Optional<Like> optionalLike = likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId());
        likeServiceValidator.checkDuplicateLike(optionalLike);
        Like like = likeMapper.toEntity(likeDto);

        post.getLikes().add(like);

        Like savedLike= likeRepository.save(like);
        likeEventPublisher.publish(new LikeEvent(post.getId(), post.getAuthorId(), savedLike.getId()));
        publishEventLikePost(likeDto, post);

        kafkaProducer.sendEvent(LikeCreatedEvent.builder()
                .postId(savedLike.getPost().getId())
                .userId(savedLike.getUserId())
                .build());
        return likeMapper.toLikeDto(like);
    }

    @Transactional
    public void deleteLikeFromPost(long postId, long userId) {
        Optional<Like> like = likeRepository.findByPostIdAndUserId(postId, userId);
        likeServiceValidator.checkAvailabilityLike(like);
        Post post = postService.getPost(postId);

        post.getLikes().remove(like.get().getId());
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    @Transactional
    public LikeDto addLikeToComment(LikeDto likeDto) {
        Comment comment = commentService.getComment(likeDto.getCommentId());
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());

        Optional<Like> optionalLike = likeRepository.findByCommentIdAndUserId(comment.getId(), userDto.getId());
        likeServiceValidator.checkDuplicateLike(optionalLike);
        Like like = likeMapper.toEntity(likeDto);

        comment.getLikes().add(like);
        likeRepository.save(like);
        return likeMapper.toLikeDto(like);
    }

    @Transactional
    public void deleteLikeFromComment(long commentId, long userId) {
        Optional<Like> like = likeRepository.findByCommentIdAndUserId(commentId, userId);
        likeServiceValidator.checkAvailabilityLike(like);
        Comment comment = commentService.getComment(commentId);

        comment.getLikes().remove(like.get().getId());
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private void publishEventLikePost(LikeDto likeDto, Post post) {
        likePostPublisher.publish(likeEventMapper.mapLikePostEvent(likeDto, post.getAuthorId()));

        postLikeEventPublisher.publish(LikeEvent.builder()
                .authorLikeId(likeDto.getUserId())
                .authorPostId(post.getAuthorId())
                .postId(post.getId()).build());
    }
}