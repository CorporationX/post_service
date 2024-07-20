package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.kafka.event.State;
import faang.school.postservice.mapper.like.CommentLikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.CommentLike;
import faang.school.postservice.kafka.producer.like.CommentLikeProducer;
import faang.school.postservice.repository.CommentLikeRepository;
import faang.school.postservice.validator.like.LikeValidatorImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentLikeService implements LikeService<CommentLikeDto> {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentLikeMapper commentLikeMapper;
    private final LikeValidatorImpl likeValidator;
    private final CommentLikeProducer commentLikeProducer;

    @Override
    public CommentLikeDto addLike(long userId, long id) {

        CommentLikeDto likeDto = createLikeDto(userId, id);

        likeValidator.validateUserExistence(userId);
        Comment comment = likeValidator.validateAndGetCommentToLike(userId, id);

        CommentLike like = commentLikeMapper.toEntity(likeDto);
        like.setComment(comment);
        like = commentLikeRepository.save(like);

        commentLikeProducer.produce(commentLikeMapper.toKafkaEvent(like, State.ADD));

        log.info("Like with likeId = {} was added on comment with commentId = {} by user with userId = {}", like.getId(), id, userId);

        return commentLikeMapper.toDto(like);
    }

    @Override
    public void removeLike(long userId, long id) {

        CommentLikeDto likeDto = createLikeDto(userId, id);
        CommentLike like = commentLikeMapper.toEntity(likeDto);

        commentLikeRepository.deleteByCommentIdAndUserId(id, userId);

        commentLikeProducer.produce(commentLikeMapper.toKafkaEvent(like, State.DELETE));

        log.info("Like with likeId = {} was removed from comment with commentId = {} by user with userId = {}", like.getId(), id, userId);
    }

    private CommentLikeDto createLikeDto(Long userId, Long commentId) {
        CommentLikeDto likeDto = new CommentLikeDto();
        likeDto.setUserId(userId);
        likeDto.setCommentId(commentId);
        return likeDto;
    }
}
