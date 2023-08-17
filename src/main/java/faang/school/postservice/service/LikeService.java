package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.redis.LikeEventDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.redis.LikeEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.redis.RedisMessagePublisher;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final ObjectMapper objectMapper;
    private final RedisMessagePublisher redisMessagePublisher;
    private final LikeEventMapper likeEventMapper;
    private final ChannelTopic likeTopicName;

    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long postId = likeDto.getPostId();
        Long userId = likeDto.getUserId();
        Post post = postMapper.toEntity(postService.getPost(likeDto.getPostId()));
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setPost(post);
        likeRepository.save(like);
        LikeEventDto likeEventDto = likeEventMapper.toDto(like);
        try {
            String likeEvent = objectMapper.writeValueAsString(likeEventDto);
            redisMessagePublisher.publish(likeTopicName.getTopic(),likeEvent);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }
        log.info("Post id={} was liked by user id={}", likeDto.getPostId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("Post id={} was unliked by user id={}", postId, userId);
    }

    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long commentId = likeDto.getCommentId();
        Long userId = likeDto.getUserId();
        Comment comment = commentService.getComment(likeDto.getCommentId());
        Optional<Like> existingLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        Like like = likeMapper.toModel(likeDto);
        like.setComment(comment);
        likeRepository.save(like);
        log.info("Comment id={} was liked by user id={}", likeDto.getCommentId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    public void unlikeComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("Comment id={} was unliked by user id={}", commentId, userId);
    }
}
