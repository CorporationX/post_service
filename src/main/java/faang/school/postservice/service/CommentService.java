package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdatedCommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ErrorMessage;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publishers.kafka.KafkaCommentProducer;
import faang.school.postservice.publishers.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.redis.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CheckUserService checkUserService;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CommentEventPublisher commentEventPublisher;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final UserCacheService userCacheService;

    public CommentDto createComment(CreateCommentDto createCommentDto) {
        checkUserService.checkUserExistence(createCommentDto);
        Post post = postService.getById(createCommentDto.getPostId());

        Comment comment = commentMapper.toEntity(createCommentDto, post);
        comment = commentRepository.save(comment);
        log.info("Comment with ID = {} was created", comment.getId());
        userCacheService.save(comment.getAuthorId());
        commentEventPublisher.publish(comment);
        kafkaCommentProducer.sendMessage(comment);
        return commentMapper.toDto(comment);
    }

    public CommentDto updateComment(UpdatedCommentDto updatedCommentDto) {
        Comment comment = commentRepository.findById(updatedCommentDto.getId())
                .orElseThrow(() -> new NotFoundException("Comment with ID = " + updatedCommentDto.getId() + " does not found"));
        if (comment.getAuthorId() != updatedCommentDto.getAuthorId()) {
            throw new DataValidationException(ErrorMessage.AUTHOR_ID_NOT_CONFIRMED);
        }
        comment.setContent(updatedCommentDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with ID = {} was updated", updatedCommentDto.getId());
        return commentMapper.toDto(updatedComment);
    }

    public List<CommentDto> getAllCommentsByPostIdSortedByCreatedDate(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException("Post with ID = " + postId + "does not exist");
        }
        List<Comment> postsComments = commentRepository
                .findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
        log.info("{} comments for post with ID = {} was found and sorted by created date", postsComments.size(), postId);
        return commentMapper.toDtoList(postsComments);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
        log.info("Comment with ID = {} was deleted", id);
    }

    @Transactional(readOnly = true)
    public Comment getById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Comment id=%d not found", id)));
    }

    public List<Comment> getAllByIds(Iterable<Long> ids) {
        return commentRepository.findAllById(ids);
    }
}
