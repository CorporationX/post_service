package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.kafka.producer.KafkaLikeProducer;
import faang.school.postservice.service.redis.LikeEventPublisher;
import faang.school.postservice.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeValidator likeValidator;
    private final LikeMapper likeMapper;
    private final LikeRepository likeRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final PostMapper postMapper;
    private final LikeEventPublisher likeEventPublisher;
    private final KafkaLikeProducer kafkaLikeProducer;
    private final CommentMapper commentMapper;

    @Transactional
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
        likeEventPublisher.publish(like);
        kafkaLikeProducer.sendMessage(KafkaKey.CREATE, likeDto);
        log.info("Post id={} was liked by user id={}", likeDto.getPostId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    @Transactional
    public void unlikePost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        kafkaLikeProducer.sendMessage(KafkaKey.DELETE, LikeDto.builder()
                .postId(postId)
                .build());
        log.info("Post id={} was unliked by user id={}", postId, userId);
    }

    @Transactional
    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validateLike(likeDto);
        Long commentId = likeDto.getCommentId();
        Long userId = likeDto.getUserId();

        Optional<Like> existingLike = likeRepository.findByCommentIdAndUserId(commentId, userId);
        if (existingLike.isPresent()) {
            return likeMapper.toDto(existingLike.get());
        }
        CommentDto commentDto = commentService.getComment(likeDto.getCommentId());
        Comment comment = commentMapper.toEntity(commentDto);
        Like like = likeMapper.toModel(likeDto);

        likeDto.setPostId(commentDto.getPostId());
        like.setComment(comment);
        likeRepository.save(like);
        kafkaLikeProducer.sendMessage(KafkaKey.CREATE, likeDto);
        log.info("Comment id={} was liked by user id={}", likeDto.getCommentId(), likeDto.getUserId());
        return likeMapper.toDto(like);
    }

    @Transactional
    public void unlikeComment(long commentId, long userId) {
        likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresent(like -> {
            CommentDto commentDto = commentService.getComment(commentId);
            LikeDto likeDto = likeMapper.toDto(like);
            likeDto.setPostId(commentDto.getPostId());

            likeRepository.deleteByCommentIdAndUserId(commentId, userId);
            kafkaLikeProducer.sendMessage(KafkaKey.DELETE, likeDto);
            log.info("Comment id={} was unliked by user id={}", commentId, userId);
        });
    }
}
