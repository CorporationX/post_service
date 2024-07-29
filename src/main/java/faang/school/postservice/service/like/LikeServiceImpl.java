package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeValidator likeValidator;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final KafkaLikeProducer kafkaLikeProducer;

    @Override
    @Transactional
    public LikeDto likeComment(LikeDto likeDto) {
        likeValidator.validate(likeDto);
        if (likeDto.getCommentId() != null) {
            if (findByCommentIdAndAuthorId(likeDto) == null) {
                Like savedLike = setCommentFromDto(likeDto);
                LikeDto resultDto = likeMapper.toDto(likeRepository.save(savedLike));
                sendLikeEventToKafka(likeDto);
                return resultDto;
            }
        }
        log.warn("user with id {} try like comment with id {} second time", likeDto.getUserId(), likeDto.getCommentId());
        return null;
    }

    private void sendLikeEventToKafka(LikeDto likeDto) {
        LikeEvent event = likeMapper.toEvent(likeDto);
        kafkaLikeProducer.sendEvent(event);
    }

    @Override
    @Transactional
    public void deleteLikeFromComment(long commentId, long userId) {
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    @Override
    @Transactional
    public LikeDto likePost(LikeDto likeDto) {
        likeValidator.validate(likeDto);
        if (likeDto.getPostId() != null) {
            if (findByPostIdAndAuthorId(likeDto) == null) {
                Like savedLike = setPostFromDto(likeDto);
                savedLike.setComment(null);
                LikeDto resultDto = likeMapper.toDto(likeRepository.save(savedLike));
                sendLikeEventToKafka(likeDto);
                return resultDto;
            }
        }
        log.warn("user with id {} try like post with id {} second time", likeDto.getUserId(), likeDto.getPostId());
        return null;
    }

    @Override
    @Transactional
    public void deleteLikeFromPost(long postId, long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    private Like findByCommentIdAndAuthorId(LikeDto like) {
        return likeRepository.findByCommentIdAndUserId(like.getCommentId(), like.getUserId())
                        .orElse(null);
    }

    private Like findByPostIdAndAuthorId(LikeDto like) {
        return likeRepository.findByPostIdAndUserId(like.getPostId(), like.getUserId())
                .orElse(null);
    }

    private Like setCommentFromDto(LikeDto likeDto) {
        Like like = likeMapper.toEntity(likeDto);
        Comment comment = commentRepository.findById(likeDto.getCommentId())
                        .orElseThrow(() -> new EntityNotFoundException("Comment with id = %d not found"));
        like.setComment(comment);
        return like;
    }

    private Like setPostFromDto(LikeDto likeDto) {
        Like like = likeMapper.toEntity(likeDto);
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post with id = %d not found"));
        like.setPost(post);
        return like;
    }
}

